package com.example.bookey.data;

import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BookRepository {
    private final BookDao bookDao;
    private final ExecutorService executorService;

    public BookRepository(BookDao bookDao) {
        this.bookDao = bookDao;
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<BookEntity>> getGeneralCatalog() {
        return bookDao.getAllBooks();
    }

    public LiveData<List<PersonalBookWithDetails>> getPersonalCatalog() {
        return bookDao.getPersonalBooks();
    }

    public void seedGeneralCatalogIfNeeded() {
        executorService.execute(() -> {
            List<BookEntity> seed = new ArrayList<>();
            seed.add(new BookEntity("1984", "George Orwell", "Distopia classica sul controllo sociale."));
            seed.add(new BookEntity("Il nome della rosa", "Umberto Eco", "Thriller storico ambientato in un'abbazia medievale."));
            seed.add(new BookEntity("La coscienza di Zeno", "Italo Svevo", "Romanzo italiano sulla psicologia e l'autoanalisi."));
            seed.add(new BookEntity("Orgoglio e pregiudizio", "Jane Austen", "Romanzo sull'amore e le convenzioni sociali."));
            bookDao.insertBooks(seed);
        });
    }

    public void togglePersonalBook(long bookId, boolean selected) {
        executorService.execute(() -> {
            if (selected) {
                if (!bookDao.isInPersonalCatalog(bookId)) {
                    bookDao.insertPersonalBook(new PersonalBookEntity(bookId, 0, "", ReadingStatus.TO_READ));
                }
            } else {
                bookDao.removePersonalBook(bookId);
            }
        });
    }

    public void updatePersonalBook(PersonalBookEntity personalBookEntity) {
        executorService.execute(() -> bookDao.updatePersonalBook(personalBookEntity));
    }

    public void isInPersonalCatalog(long bookId, PersonalSelectionCallback callback) {
        executorService.execute(() -> callback.onResult(bookDao.isInPersonalCatalog(bookId)));
    }

    public interface PersonalSelectionCallback {
        void onResult(boolean isSelected);
    }
}
