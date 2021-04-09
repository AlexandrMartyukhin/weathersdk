package ru.minilan.weathersdk;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import ru.minilan.weatherlib.OnGetWeatherCallback;
import ru.minilan.weatherlib.Weather;

public class FirstFragment extends Fragment {

    private AlertDialog dialog;

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText editText = view.findViewById(R.id.edittext);

        view.findViewById(R.id.button_first).setOnClickListener(view1 -> {

            dialog = new AlertDialog.Builder(requireContext())
                    .setTitle(R.string.weather)
                    .setMessage(R.string.loading)
                    .setPositiveButton(R.string.ok, (dialogInterface, i) -> dialog.dismiss())
                    .show();

            new Weather().getCurrentWeather(editText.getText().toString(), new OnGetWeatherCallback() {
                @Override
                public void onSuccess(String data) {
                    if (dialog.isShowing()) {
                        dialog.setMessage(data);
                    }
                }

                @Override
                public void onError(Exception exception) {
                    if (dialog.isShowing()) {
                        dialog.setMessage(exception.getMessage());
                    }
                    exception.printStackTrace();
                }
            });
        });
    }
}