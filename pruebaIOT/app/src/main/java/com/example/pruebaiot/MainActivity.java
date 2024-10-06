package com.example.pruebaiot;
import android.widget.ArrayAdapter;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.AdapterView;
import android.widget.Spinner;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private boolean esVertical = false;
    private boolean rutValido = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tiempoTextView = findViewById(R.id.tiempo);
        String currentDateAndTime = new SimpleDateFormat("dd/MM/yyyy, HH:mm a", Locale.getDefault()).format(new Date());
        tiempoTextView.setText("Fecha: " + currentDateAndTime);
        EditText rutEditText = findViewById(R.id.userRut);
        Spinner spinner = findViewById(R.id.locaciones_accidente);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.locaciones_accidente, R.layout.spinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        rutEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String rut = charSequence.toString().trim();
                if (verificador.validaRut(rut)) {
                    rutEditText.setError(null);
                    rutValido = true;
                } else {
                    rutEditText.setError("RUT inválido, ingrese rut sin puntos y con guión, no es posible grabar con rut invalido");
                    rutValido = false;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Toast.makeText(this, "El dispositivo no tiene un acelerómetro", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        EditText usuarioEditText = findViewById(R.id.usuario);
        EditText rutEditText = findViewById(R.id.userRut);
        EditText descripcionEditText = findViewById(R.id.descripcion_de_accidente);
        Spinner locacionesSpinner = findViewById(R.id.locaciones_accidente);
        boolean allFieldsFilled = !usuarioEditText.getText().toString().trim().isEmpty() &&
                !rutEditText.getText().toString().trim().isEmpty() &&
                !descripcionEditText.getText().toString().trim().isEmpty() &&
                locacionesSpinner.getSelectedItemPosition() != AdapterView.INVALID_POSITION;
        if (Math.abs(x) < 2 && Math.abs(y) > 8 && Math.abs(z) < 2) {
            if (!esVertical && rutValido && allFieldsFilled) {
                Toast.makeText(this, "Datos guardados", Toast.LENGTH_SHORT).show();
                esVertical = true;
            }
        } else {
            esVertical = false;
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
}
