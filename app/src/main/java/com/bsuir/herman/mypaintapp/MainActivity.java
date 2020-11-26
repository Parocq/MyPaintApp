package com.bsuir.herman.mypaintapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import top.defaults.colorpicker.ColorPickerPopup;

public class MainActivity extends AppCompatActivity {


    private DrawingView drawView;
    private Button colorBtn;
    private Button colorPickerButton;

    private ImageButton penImButton;
    private ImageButton eraseImButton;
    private ImageButton triangleImButton;
    private ImageButton circleImButton;
    private ImageButton rectangleImButton;

    private int status = 0;

    @SuppressLint({"ResourceType"})
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawView = findViewById(R.id.drawing);
        colorBtn = findViewById(R.id.colorBtn);
        drawView.setButton(colorBtn);
        colorBtn.setBackgroundColor(Color.BLACK);
        penImButton = findViewById(R.id.draw_btn);
        eraseImButton = findViewById(R.id.erase_btn);
        triangleImButton = findViewById(R.id.shapes_btn_triangle);
        circleImButton = findViewById(R.id.shapes_btn_circle);
        rectangleImButton = findViewById(R.id.shapes_btn_rectangle);

        try {
            if (Holder.drawable == null) {
                Holder.drawable = Drawable.createFromXml(getResources(), getResources().getXml(R.drawable.btn_border));
            }
            penImButton.setForeground(Holder.drawable);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException ignored) {
        }

        colorPickerButton = findViewById(R.id.btn_main_color_picker_dialog);
        colorPickerButton.setOnClickListener(view -> showColorPickerDialog());

        /**-----------------------Seek bar pen width setup ------------------**/
        SeekBar seekBar = findViewById(R.id.seek_bar_width);
        seekBar.setMax(100);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                drawView.setSize(seekBar.getProgress());
            }
        });
        seekBar.setProgress(50);
    }

    private void showColorPickerDialog() {
        new ColorPickerPopup.Builder(this)
                .initialColor(Color.RED) // Set initial color
                .enableBrightness(true) // Enable brightness slider or not
                .enableAlpha(true) // Enable alpha slider or not
                .okTitle("Выбрать")
                .cancelTitle("Отмена")
                .showIndicator(true)
                .showValue(true)
                .build()
                .show(new ColorPickerPopup.ColorPickerObserver() {
                    @Override
                    public void onColorPicked(int color) {
                        drawView.setPaintColor("" + color);
                        colorPickerButton.setBackgroundColor(color);
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuItem1: {
                drawView.recreate();
                break;
            }
            case R.id.menuItem2: {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 800);
                break;
            }
            case R.id.menuItem3: {
                savePainting();
                break;
            }
        }
        return true;
    }

    public void savePainting() {
        AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
        saveDialog.setTitle("Сохрание");
        saveDialog.setMessage("Сохранить рисунок на устройстве?");
        saveDialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //save drawing
                drawView.setDrawingCacheEnabled(true);
                String imgSaved = MediaStore.Images.Media.insertImage(
                        getContentResolver(), drawView.getDrawingCache(),
                        UUID.randomUUID().toString() + ".png", "drawing");
                if (imgSaved != null) {
                    Toast.makeText(getApplicationContext(),
                            "Сохранено.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Изображение не сохранено.", Toast.LENGTH_SHORT).show();

                }
                // Destroy the current cache.
                drawView.destroyDrawingCache();
            }
        });
        saveDialog.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        saveDialog.show();
    }


    public void onDefaultColorChased(View view) {
        Button v = (Button) view;
        int color = v.getCurrentTextColor();
        drawView.setPaintColor("" + color);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onRectangleShape(View view) {
        status = 1;
        drawView.setStatus(1);
        configureButtons(1);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onOvalShape(View view) {
        status = 2;
        drawView.setStatus(2);
        configureButtons(2);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onTriangleShape(View view) {
        status = 3;
        drawView.setStatus(3);
        configureButtons(3);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onDraw(View view) {
        drawView.setErase(false);
        status = 0;
        drawView.setStatus(0);
        configureButtons(4);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onErase(View view) {
        drawView.setErase(true);
        status = 0;
        drawView.setStatus(0);
        configureButtons(5);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void configureButtons(int num) {
        penImButton.setForeground(null);
        eraseImButton.setForeground(null);
        triangleImButton.setForeground(null);
        circleImButton.setForeground(null);
        rectangleImButton.setForeground(null);
        switch (num) {
            case 1:
                drawView.setErase(false);
                rectangleImButton.setForeground(Holder.drawable);
                break;
            case 2:
                drawView.setErase(false);
                circleImButton.setForeground(Holder.drawable);
                break;
            case 3:
                drawView.setErase(false);
                triangleImButton.setForeground(Holder.drawable);
                break;
            case 4:
                penImButton.setForeground(Holder.drawable);
                break;
            case 5:
                eraseImButton.setForeground(Holder.drawable);
                break;
        }
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                drawView.loadFile(selectedImage);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}