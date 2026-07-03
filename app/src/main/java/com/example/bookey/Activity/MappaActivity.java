package com.example.bookey.Activity;

import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookey.Entity.CatalogoPersonaleEntity;
import com.example.bookey.Entity.FriendRequestEntity;
import com.example.bookey.Entity.LibroEntity;
import com.example.bookey.Entity.MessageEntity;
import com.example.bookey.Entity.SharedBookEntity;
import com.example.bookey.Entity.UserEntity;
import com.example.bookey.Entity.UserLocationEntity;
import com.example.bookey.Model.UserMapData;
import com.example.bookey.R;
import com.example.bookey.data.AppDatabase;
import com.example.bookey.ui.FriendListAdapter;
import com.example.bookey.ui.FriendRequestAdapter;
import com.example.bookey.ui.LibroPersonaleUI;
import com.example.bookey.ui.MessageAdapter;
import com.example.bookey.ui.SelectableBookAdapter;
import com.example.bookey.ui.SharedBooksMiniAdapter;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MappaActivity extends AppCompatActivity {

    private TextView locationText;
    private FusedLocationProviderClient fusedLocationClient;
    private MapView mapView;
    private MyLocationNewOverlay myLocationOverlay;
    private FloatingActionButton shareLocationFab;
    private FloatingActionButton friendRequestsFab;
    private MaterialButtonToggleGroup radiusToggleGroup;

    private AppDatabase appDatabase;
    private ExecutorService dbExecutor;
    private String currentUserId;
    private Location currentLocation;
    private int selectedRadiusKm = 10; // Default 10km

    private final ActivityResultLauncher<String> locationPermissionRequest =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    fetchDeviceLocation();
                } else {
                    locationText.setText(R.string.location_permission_denied);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        Configuration.getInstance().setUserAgentValue(getPackageName());

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_map);


        appDatabase = AppDatabase.getInstance(this);
        dbExecutor = Executors.newSingleThreadExecutor();
        currentUserId = getIntent().getStringExtra("extra_user_id");

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        Button requestLocationButton = findViewById(R.id.requestLocationButton);
        locationText = findViewById(R.id.locationStatusTextView);
        shareLocationFab = findViewById(R.id.shareLocationFab);
        radiusToggleGroup = findViewById(R.id.radiusToggleGroup);


        mapView = findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        IMapController mapController = mapView.getController();
        mapController.setZoom(15.0);


        myLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), mapView);
        myLocationOverlay.enableMyLocation();
        myLocationOverlay.enableFollowLocation();
        mapView.getOverlays().add(myLocationOverlay);

        requestLocationButton.setOnClickListener(v -> checkLocationPermissionAndFetch());


        shareLocationFab.setOnClickListener(v -> showShareLocationDialog());


        friendRequestsFab = findViewById(R.id.friendRequestsFab);
        friendRequestsFab.setOnClickListener(v -> showFriendRequestsDialog());


        radiusToggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.radius10kmButton) {
                    selectedRadiusKm = 10;
                } else if (checkedId == R.id.radius100kmButton) {
                    selectedRadiusKm = 100;
                } else if (checkedId == R.id.radius1000kmButton) {
                    selectedRadiusKm = 1000;
                }
                loadAndDisplayNearbyUsers();
            }
        });


        checkLocationPermissionAndFetch();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDetach();
        }
    }

    private void checkLocationPermissionAndFetch() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Permesso già concesso - mostra un feedback
            Toast.makeText(this, "Accesso alla posizione già concesso", Toast.LENGTH_SHORT).show();
            fetchDeviceLocation();
        } else {
            // Richiedi il permesso
            locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void fetchDeviceLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(this, this::handleLocation)
                .addOnFailureListener(e -> locationText.setText(getString(R.string.location_error, e.getMessage())));
    }

    private void handleLocation(Location location) {
        if (location == null) {
            locationText.setText(R.string.location_not_available);
            return;
        }

        currentLocation = location;

        String message = String.format(
                Locale.ITALY,
                getString(R.string.location_coordinates),
                location.getLatitude(),
                location.getLongitude()
        );
        locationText.setText(message);

        // Centra la mappa sulla posizione corrente
        if (mapView != null) {
            GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
            IMapController mapController = mapView.getController();
            mapController.setCenter(geoPoint);
            mapController.setZoom(15.0);
            mapView.invalidate();
        }


        loadAndDisplayNearbyUsers();
    }

    private void showShareLocationDialog() {
        if (currentUserId == null) {
            Toast.makeText(this, "Errore: utente non autenticato", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentLocation == null) {
            Toast.makeText(this, "Richiedi prima la tua posizione", Toast.LENGTH_SHORT).show();
            return;
        }


        dbExecutor.execute(() -> {
            List<CatalogoPersonaleEntity> catalogoEntries = appDatabase.bookDao()
                    .getPersonalCatalogByUserId(currentUserId);

            if (catalogoEntries.isEmpty()) {
                runOnUiThread(() -> Toast.makeText(this,
                        "Aggiungi prima alcuni libri al tuo catalogo personale", Toast.LENGTH_LONG).show());
                return;
            }

            List<LibroPersonaleUI> books = new ArrayList<>();
            for (CatalogoPersonaleEntity entry : catalogoEntries) {
                LibroEntity libro = appDatabase.bookDao().getBookByIsbn(entry.bookIsbn);
                if (libro != null) {
                    books.add(new LibroPersonaleUI(
                            libro.isbn,
                            libro.titolo,
                            libro.autore,

                            libro.coverUrl,
                            entry.readingStatus
                    ));
                }
            }

            runOnUiThread(() -> showBookSelectionDialog(books));
        });
    }

    private void showBookSelectionDialog(List<LibroPersonaleUI> books) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_select_books, null);

        RecyclerView recyclerView = dialogView.findViewById(R.id.selectBooksRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        SelectableBookAdapter adapter = new SelectableBookAdapter(books, 5);
        recyclerView.setAdapter(adapter);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        dialogView.findViewById(R.id.cancelButton).setOnClickListener(v -> dialog.dismiss());

        dialogView.findViewById(R.id.confirmButton).setOnClickListener(v -> {
            List<String> selectedIsbns = adapter.getSelectedIsbns();
            saveLocationAndBooks(selectedIsbns, books);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void saveLocationAndBooks(List<String> selectedIsbns, List<LibroPersonaleUI> allBooks) {
        dbExecutor.execute(() -> {
            try {
                // Salva la posizione
                UserLocationEntity locationEntity = new UserLocationEntity(
                        currentUserId,
                        currentLocation.getLatitude(),
                        currentLocation.getLongitude(),
                        System.currentTimeMillis()
                );
                appDatabase.locationDao().insertOrUpdateLocation(locationEntity);

                // Rimuovi i libri condivisi precedenti
                appDatabase.locationDao().deleteAllSharedBooksByUser(currentUserId);

                // Prepara la lista dei libri da condividere
                List<String> isbnsToShare = new ArrayList<>();

                // Aggiungi i libri selezionati manualmente
                isbnsToShare.addAll(selectedIsbns);

                // se ne selezioniamo meno di 5 ma ne abbiamo più di 5 nel catalogo i rimanenti
                // verranno selezionati casualemente
                if (isbnsToShare.size() < 5) {
                    List<String> remainingIsbns = new ArrayList<>();
                    for (LibroPersonaleUI book : allBooks) {
                        if (!isbnsToShare.contains(book.isbn)) {
                            remainingIsbns.add(book.isbn);
                        }
                    }

                    //  aggiungi fino a 5 libri totali da mostrare
                    Collections.shuffle(remainingIsbns);
                    int toAdd = Math.min(5 - isbnsToShare.size(), remainingIsbns.size());
                    for (int i = 0; i < toAdd; i++) {
                        isbnsToShare.add(remainingIsbns.get(i));
                    }
                }

                // Salva i libri condivisi
                for (int i = 0; i < isbnsToShare.size(); i++) {
                    SharedBookEntity sharedBook = new SharedBookEntity(
                            currentUserId,
                            isbnsToShare.get(i),
                            i + 1,
                            selectedIsbns.contains(isbnsToShare.get(i))
                    );
                    appDatabase.locationDao().insertSharedBook(sharedBook);
                }

                runOnUiThread(() -> {
                    Toast.makeText(this, "Posizione condivisa con successo!", Toast.LENGTH_SHORT).show();
                    loadAndDisplayNearbyUsers();
                });

            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Errore nel salvataggio: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }

    private void loadAndDisplayNearbyUsers() {
        if (currentLocation == null || currentUserId == null) {
            return;
        }

        dbExecutor.execute(() -> {
            try {
                // Carica tutte le posizioni tranne quella dell'utente corrente
                List<UserLocationEntity> allLocations = appDatabase.locationDao()
                        .getAllLocationsExcept(currentUserId);
                
                // Log per debug
                System.out.println("DEBUG: Trovate " + allLocations.size() + " posizioni totali nel database");

                List<UserMapData> nearbyUsers = new ArrayList<>();

                for (UserLocationEntity userLocation : allLocations) {
                    // Calcola la distanza
                    float[] results = new float[1];
                    Location.distanceBetween(
                            currentLocation.getLatitude(),
                            currentLocation.getLongitude(),
                            userLocation.latitude,
                            userLocation.longitude,
                            results
                    );

                    float distanceKm = results[0] / 1000f;
                    
                    System.out.println("DEBUG: Utente " + userLocation.userId + " a distanza " + distanceKm + " km");

                    // Filtra per raggio selezionato
                    if (distanceKm <= selectedRadiusKm) {

                        UserEntity userEntity = appDatabase.userDao().getUserByUserId(userLocation.userId);


                        List<SharedBookEntity> sharedBookEntries = appDatabase.locationDao()
                                .getSharedBooksByUserId(userLocation.userId);
                        
                        System.out.println("DEBUG: Utente " + userLocation.userId + " ha " + sharedBookEntries.size() + " libri condivisi");

                        List<LibroEntity> sharedBooks = new ArrayList<>();
                        for (SharedBookEntity entry : sharedBookEntries) {
                            LibroEntity libro = appDatabase.bookDao().getBookByIsbn(entry.bookIsbn);
                            if (libro != null) {
                                sharedBooks.add(libro);
                            }
                        }

                        if (userEntity != null) {
                            nearbyUsers.add(new UserMapData(
                                    userEntity.userId,
                                    userEntity.displayName,
                                    userLocation.latitude,
                                    userLocation.longitude,
                                    sharedBooks
                            ));
                        }
                    }
                }

                runOnUiThread(() -> displayUsersOnMap(nearbyUsers));

            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Errore nel caricamento degli utenti: " + e.getMessage(),
                                Toast.LENGTH_LONG).show());
            }
        });
    }

    private void displayUsersOnMap(List<UserMapData> users) {
        if (mapView == null) return;


        List<Marker> markersToRemove = new ArrayList<>();
        for (int i = 0; i < mapView.getOverlays().size(); i++) {
            if (mapView.getOverlays().get(i) instanceof Marker) {
                markersToRemove.add((Marker) mapView.getOverlays().get(i));
            }
        }
        mapView.getOverlays().removeAll(markersToRemove);

        //  marker sulla mappa per ogni utente
        for (UserMapData userData : users) {
            Marker marker = new Marker(mapView);
            GeoPoint position = new GeoPoint(userData.latitude, userData.longitude);
            marker.setPosition(position);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setTitle(userData.displayName);


            StringBuilder snippet = new StringBuilder();
            int count = Math.min(userData.sharedBooks.size(), 5);
            for (int i = 0; i < count; i++) {
                LibroEntity book = userData.sharedBooks.get(i);
                snippet.append("• ").append(book.titolo).append("\n");
            }
            marker.setSnippet(snippet.toString().trim());

            // Aggiungi listener per mostrare un dialog con più dettagli
            marker.setOnMarkerClickListener((clickedMarker, mapView) -> {
                showUserDetailsDialog(userData);
                return true;
            });

            mapView.getOverlays().add(marker);
        }

        mapView.invalidate();

        Toast.makeText(this,
                "Trovati " + users.size() + " utenti nel raggio di " + selectedRadiusKm + "km",
                Toast.LENGTH_SHORT).show();
    }

    private void showUserDetailsDialog(UserMapData userData) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.marker_popup, null);

        TextView userNameTextView = dialogView.findViewById(R.id.userNameTextView);
        RecyclerView recyclerView = dialogView.findViewById(R.id.sharedBooksRecyclerView);
        TextView friendStatusTextView = dialogView.findViewById(R.id.friendStatusTextView);
        MaterialButton addFriendButton = dialogView.findViewById(R.id.addFriendButton);
        MaterialButton openChatButton = dialogView.findViewById(R.id.openChatButton);

        userNameTextView.setText(userData.displayName);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        SharedBooksMiniAdapter adapter = new SharedBooksMiniAdapter(userData.sharedBooks);
        recyclerView.setAdapter(adapter);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        // Controlla lo stato dell'amicizia in background
        dbExecutor.execute(() -> {
            FriendRequestEntity existing = appDatabase.friendDao()
                    .getExistingRequest(currentUserId, userData.userId);

            runOnUiThread(() -> {
                if (existing == null) {
                    // Nessuna richiesta: mostra bottone "Aggiungi amico"
                    addFriendButton.setVisibility(View.VISIBLE);
                    openChatButton.setVisibility(View.GONE);
                    friendStatusTextView.setVisibility(View.GONE);
                } else if ("PENDING".equals(existing.status)) {
                    addFriendButton.setVisibility(View.GONE);
                    openChatButton.setVisibility(View.GONE);
                    friendStatusTextView.setVisibility(View.VISIBLE);
                    friendStatusTextView.setText("Richiesta di amicizia in sospeso...");
                } else if ("ACCEPTED".equals(existing.status)) {
                    addFriendButton.setVisibility(View.GONE);
                    openChatButton.setVisibility(View.VISIBLE);
                    friendStatusTextView.setVisibility(View.VISIBLE);
                    friendStatusTextView.setText("✓ Siete amici");
                } else if ("REJECTED".equals(existing.status)) {
                    addFriendButton.setVisibility(View.VISIBLE);
                    openChatButton.setVisibility(View.GONE);
                    friendStatusTextView.setVisibility(View.VISIBLE);
                    friendStatusTextView.setText("Richiesta precedente rifiutata");
                }
            });
        });

        addFriendButton.setOnClickListener(v -> {
            sendFriendRequest(userData.userId, userData.displayName);
            addFriendButton.setEnabled(false);
            addFriendButton.setText("Richiesta inviata");
            friendStatusTextView.setVisibility(View.VISIBLE);
            friendStatusTextView.setText("Richiesta di amicizia in sospeso...");
        });

        openChatButton.setOnClickListener(v -> {
            dialog.dismiss();
            showChatDialog(userData.userId, userData.displayName);
        });

        dialog.show();
    }

    // ══════════════════════════════════════════════
    //  SISTEMA RICHIESTE DI AMICIZIA
    // ══════════════════════════════════════════════

    private void sendFriendRequest(String toUserId, String toUserName) {
        dbExecutor.execute(() -> {
            // Controlla che non esista già
            FriendRequestEntity existing = appDatabase.friendDao()
                    .getExistingRequest(currentUserId, toUserId);

            if (existing != null && "ACCEPTED".equals(existing.status)) {
                runOnUiThread(() -> Toast.makeText(this, "Siete già amici!", Toast.LENGTH_SHORT).show());
                return;
            }

            if (existing != null && "PENDING".equals(existing.status)) {
                runOnUiThread(() -> Toast.makeText(this, "Richiesta già inviata", Toast.LENGTH_SHORT).show());
                return;
            }

            // Se rifiutata, aggiorna a PENDING
            if (existing != null && "REJECTED".equals(existing.status)) {
                appDatabase.friendDao().updateRequestStatus(existing.id, "PENDING");
                runOnUiThread(() -> Toast.makeText(this,
                        "Richiesta inviata a " + toUserName, Toast.LENGTH_SHORT).show());
                return;
            }

            // Crea nuova richiesta
            FriendRequestEntity request = new FriendRequestEntity(
                    currentUserId, toUserId, "PENDING", System.currentTimeMillis());
            appDatabase.friendDao().sendFriendRequest(request);

            runOnUiThread(() -> Toast.makeText(this,
                    "Richiesta di amicizia inviata a " + toUserName, Toast.LENGTH_SHORT).show());
        });
    }

    private void showFriendRequestsDialog() {
        if (currentUserId == null) {
            Toast.makeText(this, "Errore: utente non autenticato", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_friend_requests, null);

        TabLayout tabLayout = dialogView.findViewById(R.id.friendTabLayout);
        View requestsSection = dialogView.findViewById(R.id.requestsSection);
        View friendsListSection = dialogView.findViewById(R.id.friendsListSection);
        RecyclerView requestsRecycler = dialogView.findViewById(R.id.friendRequestsRecyclerView);
        RecyclerView friendsRecycler = dialogView.findViewById(R.id.friendsListRecyclerView);
        TextView noRequestsText = dialogView.findViewById(R.id.noRequestsText);
        TextView noFriendsText = dialogView.findViewById(R.id.noFriendsText);

        requestsRecycler.setLayoutManager(new LinearLayoutManager(this));
        friendsRecycler.setLayoutManager(new LinearLayoutManager(this));

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        // Tab switching
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    requestsSection.setVisibility(View.VISIBLE);
                    friendsListSection.setVisibility(View.GONE);
                } else {
                    requestsSection.setVisibility(View.GONE);
                    friendsListSection.setVisibility(View.VISIBLE);
                    loadFriendsList(friendsRecycler, noFriendsText, dialog);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        // Carica le richieste in sospeso
        loadPendingRequests(requestsRecycler, noRequestsText, dialog);

        dialog.show();
    }

    private void loadPendingRequests(RecyclerView recyclerView, TextView emptyText, AlertDialog parentDialog) {
        dbExecutor.execute(() -> {
            List<FriendRequestEntity> pendingRequests = appDatabase.friendDao()
                    .getPendingRequestsForUser(currentUserId);

            // Carica i nomi degli utenti
            Map<String, String> userNames = new HashMap<>();
            for (FriendRequestEntity req : pendingRequests) {
                UserEntity user = appDatabase.userDao().getUserByUserId(req.fromUserId);
                if (user != null) {
                    userNames.put(req.fromUserId, user.displayName != null ? user.displayName : user.email);
                }
            }

            runOnUiThread(() -> {
                if (pendingRequests.isEmpty()) {
                    emptyText.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    emptyText.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);

                    FriendRequestAdapter adapter = new FriendRequestAdapter(pendingRequests, userNames,
                            new FriendRequestAdapter.OnRequestActionListener() {
                                @Override
                                public void onAccept(FriendRequestEntity request) {
                                    handleRequestAction(request, "ACCEPTED", recyclerView, emptyText, parentDialog);
                                }

                                @Override
                                public void onReject(FriendRequestEntity request) {
                                    handleRequestAction(request, "REJECTED", recyclerView, emptyText, parentDialog);
                                }
                            });
                    recyclerView.setAdapter(adapter);
                }
            });
        });
    }

    private void handleRequestAction(FriendRequestEntity request, String newStatus,
                                      RecyclerView recyclerView, TextView emptyText, AlertDialog parentDialog) {
        dbExecutor.execute(() -> {
            appDatabase.friendDao().updateRequestStatus(request.id, newStatus);

            runOnUiThread(() -> {
                String msg = "ACCEPTED".equals(newStatus) ? "Richiesta accettata!" : "Richiesta rifiutata";
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                // Ricarica la lista
                loadPendingRequests(recyclerView, emptyText, parentDialog);
            });
        });
    }

    private void loadFriendsList(RecyclerView recyclerView, TextView emptyText, AlertDialog parentDialog) {
        dbExecutor.execute(() -> {
            List<FriendRequestEntity> acceptedFriends = appDatabase.friendDao()
                    .getAcceptedFriends(currentUserId);

            List<String[]> friendsList = new ArrayList<>();
            for (FriendRequestEntity fr : acceptedFriends) {
                // L'amico è l'altro utente
                String friendId = fr.fromUserId.equals(currentUserId) ? fr.toUserId : fr.fromUserId;
                UserEntity friendUser = appDatabase.userDao().getUserByUserId(friendId);
                if (friendUser != null) {
                    String name = friendUser.displayName != null ? friendUser.displayName : friendUser.email;
                    friendsList.add(new String[]{friendId, name});
                }
            }

            runOnUiThread(() -> {
                if (friendsList.isEmpty()) {
                    emptyText.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    emptyText.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);

                    FriendListAdapter adapter = new FriendListAdapter(friendsList, (friendUserId, friendName) -> {
                        parentDialog.dismiss();
                        showChatDialog(friendUserId, friendName);
                    });
                    recyclerView.setAdapter(adapter);
                }
            });
        });
    }

    // ══════════════════════════════════════════════
    //  SISTEMA MESSAGGI / CHAT
    // ══════════════════════════════════════════════

    private void showChatDialog(String otherUserId, String otherUserName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_chat, null);

        TextView chatTitle = dialogView.findViewById(R.id.chatTitleTextView);
        RecyclerView messagesRecycler = dialogView.findViewById(R.id.messagesRecyclerView);
        EditText messageInput = dialogView.findViewById(R.id.messageEditText);
        ImageButton sendButton = dialogView.findViewById(R.id.sendMessageButton);

        chatTitle.setText("Chat con " + otherUserName);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        messagesRecycler.setLayoutManager(layoutManager);

        builder.setView(dialogView);
        AlertDialog dialog = builder.create();


        dbExecutor.execute(() -> {
            List<MessageEntity> messages = appDatabase.friendDao()
                    .getConversation(currentUserId, otherUserId);


            appDatabase.friendDao().markMessagesAsRead(currentUserId, otherUserId);

            runOnUiThread(() -> {
                MessageAdapter messageAdapter = new MessageAdapter(messages, currentUserId);
                messagesRecycler.setAdapter(messageAdapter);


                if (!messages.isEmpty()) {
                    messagesRecycler.scrollToPosition(messages.size() - 1);
                }


                sendButton.setOnClickListener(v -> {
                    String text = messageInput.getText().toString().trim();
                    if (text.isEmpty()) return;

                    messageInput.setText("");

                    MessageEntity newMessage = new MessageEntity(
                            currentUserId, otherUserId, text,
                            System.currentTimeMillis(), false);


                    messageAdapter.addMessage(newMessage);
                    messagesRecycler.scrollToPosition(messageAdapter.getItemCount() - 1);


                    dbExecutor.execute(() -> appDatabase.friendDao().sendMessage(newMessage));
                });
            });
        });

        dialog.show();
    }

}
