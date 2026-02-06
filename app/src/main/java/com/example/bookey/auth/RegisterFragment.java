package com.example.bookey.auth;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bookey.R;

public class RegisterFragment extends Fragment {

    private AuthNavigation authNavigation;

    public interface AuthNavigation {
        void onAuthSuccess();

        void onSwitchToLogin();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AuthNavigation) {
            authNavigation = (AuthNavigation) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_register, container, false);
        EditText etName = root.findViewById(R.id.etRegisterName);
        EditText etEmail = root.findViewById(R.id.etRegisterEmail);
        EditText etPassword = root.findViewById(R.id.etRegisterPassword);
        Button btnRegister = root.findViewById(R.id.btnRegister);
        TextView tvSwitch = root.findViewById(R.id.tvGoLogin);

        btnRegister.setOnClickListener(v -> {
            if (TextUtils.isEmpty(etName.getText()) || TextUtils.isEmpty(etEmail.getText()) || TextUtils.isEmpty(etPassword.getText())) {
                Toast.makeText(requireContext(), R.string.error_empty_fields, Toast.LENGTH_SHORT).show();
                return;
            }
            if (authNavigation != null) {
                authNavigation.onAuthSuccess();
            }
        });

        tvSwitch.setOnClickListener(v -> {
            if (authNavigation != null) {
                authNavigation.onSwitchToLogin();
            }
        });

        return root;
    }
}
