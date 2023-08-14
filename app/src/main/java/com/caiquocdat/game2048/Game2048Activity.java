package com.caiquocdat.game2048;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.caiquocdat.game2048.databinding.ActivityGame2048Binding;
import com.caiquocdat.game2048.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game2048Activity extends AppCompatActivity {
    private ActivityGame2048Binding activityGame2048Binding;
    private int matrix[][], luumatrix[][];
    private int scores = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityGame2048Binding= ActivityGame2048Binding.inflate(getLayoutInflater());
        View view = activityGame2048Binding.getRoot();
        setContentView(view);
        hideSystemUI();
        mapping();
        try {
           khoitao();
        } catch (Exception e) {
            khoitao();
        }
        final GestureDetector gestureDetector = new GestureDetector(this, new CuChiManHinh());
        activityGame2048Binding.khungchoi.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                int diemCao = getDiemcao2048();
                String diemCurrentString= activityGame2048Binding.textPoint.getText().toString();
                int diemCurrent=Integer.valueOf(diemCurrentString);
                if (diemCurrent>diemCao){
                    setDiemcao2048(diemCurrent);
                    Log.d("Test_3", "onFling: "+diemCurrent);
                    Log.d("Test_3", "onFling: "+diemCao);
                activityGame2048Binding.textMaxpoint.setText(diemCurrent+"");
                }
                return true;
            }
        });
        activityGame2048Binding.imgHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        activityGame2048Binding.imgUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trove1buoc();
            }
        });
        activityGame2048Binding.imgAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RESETGAME();
            }
        });
    }
    public void RESETGAME() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Bạn có muốn chơi lại!!").setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        khoitao();
                    }
                })
                .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                }).show();
    }
    public void trove1buoc() {
        boolean check = true;
        for (int i = 1; i < 42; i++) // Điều chỉnh giới hạn vòng lặp
        {
            if (matrix[i / 6][i % 6] != luumatrix[i / 6][i % 6]) {
                check = false;
                break;
            }
        }
        if (check == true) {
            Toast.makeText(this, "Không thể quay lại", Toast.LENGTH_SHORT).show();
            return;
        }
        for (int i = 1; i < 42; i++) // Điều chỉnh giới hạn vòng lặp
            matrix[i / 6][i % 6] = luumatrix[i / 6][i % 6];
        setBOX();
    }

    private void mapping() {
        matrix = new int[7][6];
        luumatrix = new int[7][6];
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        |View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();
    }
    private void checkForWin() {
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 6; j++) {
                if (matrix[i][j] == 2048) {
                    showWinDialog();
                    return;
                }
            }
        }
    }

    private void showWinDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflate the custom layout
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_2048_win, null);

        // Set up the AlertDialog
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);


        // Set up the custom action button
        ImageView playImg = dialogView.findViewById(R.id.playImg);
        playImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Do your custom action here
                khoitao();
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }


    public void setBOX() {
        activityGame2048Binding.textPoint.setText(String.valueOf(scores));
        activityGame2048Binding.box11.setImageResource(getBackground(matrix[1][1]));
        activityGame2048Binding.box12.setImageResource(getBackground(matrix[1][2]));
        activityGame2048Binding.box13.setImageResource(getBackground(matrix[1][3]));
        activityGame2048Binding.box14.setImageResource(getBackground(matrix[1][4]));
        activityGame2048Binding.box15.setImageResource(getBackground(matrix[1][5]));

        activityGame2048Binding.box21.setImageResource(getBackground(matrix[2][1]));
        activityGame2048Binding.box22.setImageResource(getBackground(matrix[2][2]));
        activityGame2048Binding.box23.setImageResource(getBackground(matrix[2][3]));
        activityGame2048Binding.box24.setImageResource(getBackground(matrix[2][4]));
        activityGame2048Binding.box25.setImageResource(getBackground(matrix[2][5]));

        activityGame2048Binding.box31.setImageResource(getBackground(matrix[3][1]));
        activityGame2048Binding.box32.setImageResource(getBackground(matrix[3][2]));
        activityGame2048Binding.box33.setImageResource(getBackground(matrix[3][3]));
        activityGame2048Binding.box34.setImageResource(getBackground(matrix[3][4]));
        activityGame2048Binding.box35.setImageResource(getBackground(matrix[3][5]));

        activityGame2048Binding.box41.setImageResource(getBackground(matrix[4][1]));
        activityGame2048Binding.box42.setImageResource(getBackground(matrix[4][2]));
        activityGame2048Binding.box43.setImageResource(getBackground(matrix[4][3]));
        activityGame2048Binding.box44.setImageResource(getBackground(matrix[4][4]));
        activityGame2048Binding.box45.setImageResource(getBackground(matrix[4][5]));

        activityGame2048Binding.box51.setImageResource(getBackground(matrix[5][1]));
        activityGame2048Binding.box52.setImageResource(getBackground(matrix[5][2]));
        activityGame2048Binding.box53.setImageResource(getBackground(matrix[5][3]));
        activityGame2048Binding.box54.setImageResource(getBackground(matrix[5][4]));
        activityGame2048Binding.box55.setImageResource(getBackground(matrix[5][5]));

        activityGame2048Binding.box61.setImageResource(getBackground(matrix[6][1]));
        activityGame2048Binding.box62.setImageResource(getBackground(matrix[6][2]));
        activityGame2048Binding.box63.setImageResource(getBackground(matrix[6][3]));
        activityGame2048Binding.box64.setImageResource(getBackground(matrix[6][4]));
        activityGame2048Binding.box65.setImageResource(getBackground(matrix[6][5]));
        String matrixS[][] = new String[7][6];
        for (int i = 0; i < 42; i++) {
            if (matrix[i / 6][i % 6] == 0) matrixS[i / 6][i % 6] = "";
            else matrixS[i / 6][i % 6] = String.valueOf(matrix[i / 6][i % 6]);
        }
        setSizetext();
        activityGame2048Binding.text11.setText(matrixS[1][1]);
        activityGame2048Binding.text12.setText(matrixS[1][2]);
        activityGame2048Binding.text13.setText(matrixS[1][3]);
        activityGame2048Binding.text14.setText(matrixS[1][4]);
        activityGame2048Binding.text15.setText(matrixS[1][5]);
        activityGame2048Binding.text21.setText(matrixS[2][1]);
        activityGame2048Binding.text22.setText(matrixS[2][2]);
        activityGame2048Binding.text23.setText(matrixS[2][3]);
        activityGame2048Binding.text24.setText(matrixS[2][4]);
        activityGame2048Binding.text25.setText(matrixS[2][5]);
        activityGame2048Binding.text31.setText(matrixS[3][1]);
        activityGame2048Binding.text32.setText(matrixS[3][2]);
        activityGame2048Binding.text33.setText(matrixS[3][3]);
        activityGame2048Binding.text34.setText(matrixS[3][4]);
        activityGame2048Binding.text35.setText(matrixS[3][5]);
        activityGame2048Binding.text41.setText(matrixS[4][1]);
        activityGame2048Binding.text42.setText(matrixS[4][2]);
        activityGame2048Binding.text43.setText(matrixS[4][3]);
        activityGame2048Binding.text44.setText(matrixS[4][4]);
        activityGame2048Binding.text45.setText(matrixS[4][5]);
        activityGame2048Binding.text51.setText(matrixS[5][1]);
        activityGame2048Binding.text52.setText(matrixS[5][2]);
        activityGame2048Binding.text53.setText(matrixS[5][3]);
        activityGame2048Binding.text54.setText(matrixS[5][4]);
        activityGame2048Binding.text55.setText(matrixS[5][5]);
        activityGame2048Binding.text61.setText(matrixS[6][1]);
        activityGame2048Binding.text62.setText(matrixS[6][2]);
        activityGame2048Binding.text63.setText(matrixS[6][3]);
        activityGame2048Binding.text64.setText(matrixS[6][4]);
        activityGame2048Binding.text65.setText(matrixS[6][5]);
    }

    public void setSizetext() {
        if (matrix[1][1] <= 512) activityGame2048Binding.text11.setTextSize(15);
        else activityGame2048Binding.text11.setTextSize(8);
        if (matrix[2][1] <= 512) activityGame2048Binding.text21.setTextSize(15);
        else activityGame2048Binding.text21.setTextSize(8);
        if (matrix[3][1] <= 512) activityGame2048Binding.text31.setTextSize(15);
        else activityGame2048Binding.text31.setTextSize(8);
        if (matrix[4][1] <= 512) activityGame2048Binding.text41.setTextSize(15);
        else activityGame2048Binding.text41.setTextSize(8);
        if (matrix[5][1] <= 512) activityGame2048Binding.text51.setTextSize(15);
        else activityGame2048Binding.text51.setTextSize(8);
        if (matrix[6][1] <= 512) activityGame2048Binding.text61.setTextSize(15);
        else activityGame2048Binding.text61.setTextSize(8);

        if (matrix[1][2] <= 512) activityGame2048Binding.text12.setTextSize(15);
        else activityGame2048Binding.text12.setTextSize(8);
        if (matrix[2][2] <= 512) activityGame2048Binding.text22.setTextSize(15);
        else activityGame2048Binding.text22.setTextSize(8);
        if (matrix[3][2] <= 512) activityGame2048Binding.text32.setTextSize(15);
        else activityGame2048Binding.text32.setTextSize(8);
        if (matrix[4][2] <= 512) activityGame2048Binding.text42.setTextSize(15);
        else activityGame2048Binding.text42.setTextSize(8);
        if (matrix[5][2] <= 512) activityGame2048Binding.text52.setTextSize(15);
        else activityGame2048Binding.text52.setTextSize(8);
        if (matrix[6][2] <= 512) activityGame2048Binding.text62.setTextSize(15);
        else activityGame2048Binding.text62.setTextSize(8);

        if (matrix[1][3] <= 512) activityGame2048Binding.text13.setTextSize(15);
        else activityGame2048Binding.text13.setTextSize(8);
        if (matrix[2][3] <= 512) activityGame2048Binding.text23.setTextSize(15);
        else activityGame2048Binding.text23.setTextSize(8);
        if (matrix[3][3] <= 512) activityGame2048Binding.text33.setTextSize(15);
        else activityGame2048Binding.text33.setTextSize(8);
        if (matrix[4][3] <= 512) activityGame2048Binding.text43.setTextSize(15);
        else activityGame2048Binding.text43.setTextSize(8);
        if (matrix[5][3] <= 512) activityGame2048Binding.text53.setTextSize(15);
        else activityGame2048Binding.text53.setTextSize(8);
        if (matrix[6][3] <= 512) activityGame2048Binding.text63.setTextSize(15);
        else activityGame2048Binding.text63.setTextSize(8);


        if (matrix[1][4] <= 512) activityGame2048Binding.text14.setTextSize(15);
        else activityGame2048Binding.text14.setTextSize(8);
        if (matrix[2][4] <= 512) activityGame2048Binding.text24.setTextSize(15);
        else activityGame2048Binding.text24.setTextSize(8);
        if (matrix[3][4] <= 512) activityGame2048Binding.text34.setTextSize(15);
        else activityGame2048Binding.text34.setTextSize(8);
        if (matrix[4][4] <= 512) activityGame2048Binding.text44.setTextSize(15);
        else activityGame2048Binding.text44.setTextSize(8);
        if (matrix[5][4] <= 512) activityGame2048Binding.text54.setTextSize(15);
        else activityGame2048Binding.text54.setTextSize(8);
        if (matrix[6][4] <= 512) activityGame2048Binding.text64.setTextSize(15);
        else activityGame2048Binding.text64.setTextSize(8);

        if (matrix[1][5] <= 512) activityGame2048Binding.text15.setTextSize(15);
        else activityGame2048Binding.text15.setTextSize(8);
        if (matrix[2][5] <= 512) activityGame2048Binding.text25.setTextSize(15);
        else activityGame2048Binding.text25.setTextSize(8);
        if (matrix[3][5] <= 512) activityGame2048Binding.text35.setTextSize(15);
        else activityGame2048Binding.text35.setTextSize(8);
        if (matrix[4][5] <= 512) activityGame2048Binding.text45.setTextSize(15);
        else activityGame2048Binding.text45.setTextSize(8);
        if (matrix[5][5] <= 512) activityGame2048Binding.text55.setTextSize(15);
        else activityGame2048Binding.text55.setTextSize(8);
        if (matrix[6][5] <= 512) activityGame2048Binding.text65.setTextSize(15);
        else activityGame2048Binding.text65.setTextSize(8);

    }

    public int getBackground(int n) {
        if (n == 0) return R.drawable.img_0;
        switch (n % 2048) {
            case 2:
                return R.drawable.img_2;
            case 4:
                return R.drawable.img_4;
            case 8:
                return R.drawable.img_8;
            case 16:
                return R.drawable.img_16;
            case 32:
                return R.drawable.img_32;
            case 64:
                return R.drawable.img_64;
            case 128:
                return R.drawable.img_8;
            case 256:
                return R.drawable.img_16;
            case 512:
                return R.drawable.img_32;
            case 1024:
                return R.drawable.img_64;
            case 0:
                return R.drawable.img_8;
        }
        return 0;
    }
    private void randdomnumber() {
        Random random = new Random();
        List<int[]> emptyPositions = new ArrayList<>();

        // Gather all empty positions
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 6; j++) {
                if (matrix[i][j] == 0) {
                    emptyPositions.add(new int[]{i, j});
                }
            }
        }

        // If there are no empty positions, just return
        if (emptyPositions.isEmpty()) return;

        // Choose a random empty position
        int[] chosenPosition = emptyPositions.get(random.nextInt(emptyPositions.size()));

        // Assign a value to the chosen position
        if (random.nextInt(14) < 12)
            matrix[chosenPosition[0]][chosenPosition[1]] = 2;
        else
            matrix[chosenPosition[0]][chosenPosition[1]] = 4;
    }
    public void khoitao() {

        int diemcao = getDiemcao2048();
        activityGame2048Binding.textMaxpoint.setText(String.valueOf(diemcao));

        scores = 0;
        for (int i = 0; i < 42; i++) matrix[i / 6][i % 6] = 0;
        randdomnumber();
        setLuumatrix();
        setBOX();
    }

    class CuChiManHinh extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean check = false;
            setLuumatrix();
            if (e1.getX() - e2.getX() > 100 && Math.abs(e1.getY() - e2.getY()) < Math.abs(e1.getX() - e2.getX()) && Math.abs(velocityX) > 100) {
                for (int i = 1; i <= 6; i++) {
                    for (int j = 1; j < 5; j++) {
                        if (matrix[i][j] != 0) {
                            for (int k = j + 1; k < 6; k++) {
                                if (matrix[i][k] == matrix[i][j]) {
                                    check = true;
                                    matrix[i][j] += matrix[i][k];
                                    scores += matrix[i][j];
                                    matrix[i][k] = 0;
                                    j = k;
                                    break;
                                } else if (matrix[i][k] != 0) break;
                            }
                        }
                    }
                    for (int j = 1; j < 6; j++) {
                        if (matrix[i][j] == 0) {
                            for (int k = j + 1; k < 6; k++) {
                                if (matrix[i][k] != 0) {
                                    check = true;
                                    matrix[i][j] = matrix[i][k];
                                    matrix[i][k] = 0;
                                    break;
                                }
                            }
                        }
                    }
                }
            } else if (e2.getX() - e1.getX() > 100 && Math.abs(e1.getY() - e2.getY()) < Math.abs(e1.getX() - e2.getX()) && Math.abs(velocityX) > 100) {
                for (int i = 1; i <= 6; i++) {
                    for (int j = 5; j > 0; j--) {

                        if (matrix[i][j] != 0) {
                            for (int k = j - 1; k > 0; k--) {
                                if (matrix[i][k] == matrix[i][j]) {
                                    check = true;
                                    matrix[i][j] += matrix[i][k];
                                    scores += matrix[i][j];
                                    matrix[i][k] = 0;
                                    j = k;
                                    break;
                                } else if (matrix[i][k] != 0) break;
                            }
                        }
                    }
                    for (int j = 5; j > 0; j--) {
                        if (matrix[i][j] == 0) {
                            for (int k = j - 1; k > 0; k--) {
                                if (matrix[i][k] != 0) {
                                    check = true;
                                    matrix[i][j] = matrix[i][k];
                                    matrix[i][k] = 0;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            //vuot len
            else if (e1.getY() - e2.getY() > 100 && Math.abs(e1.getX() - e2.getX()) < Math.abs(e1.getY() - e2.getY()) && Math.abs(velocityY) > 100) {
                for (int i = 1; i < 6; i++) {
                    for (int j = 1; j < 5; j++) {
                        if (matrix[j][i] != 0) {
                            for (int k = j + 1; k < 7; k++) {
                                if (matrix[k][i] == matrix[j][i]) {
                                    check = true;
                                    matrix[j][i] += matrix[k][i];
                                    scores += matrix[j][i];
                                    matrix[k][i] = 0;
                                    j = k;
                                    break;
                                }
                                if (matrix[k][i] != 0) break;
                            }
                        }
                    }
                    for (int j = 1; j < 6; j++) {
                        if (matrix[j][i] == 0) {
                            for (int k = j + 1; k < 7; k++) {
                                if (matrix[k][i] != 0) {
                                    check = true;
                                    matrix[j][i] = matrix[k][i];
                                    matrix[k][i] = 0;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            //vuot xuong
            else if (e2.getY() - e1.getY() > 100 && Math.abs(e1.getX() - e2.getX()) < Math.abs(e1.getY() - e2.getY()) && Math.abs(velocityY) > 100) {
                for (int i = 1; i < 6; i++) {
                    for (int j = 6; j >= 0; j--) {
                        if (matrix[j][i] != 0) {
                            for (int k = j - 1; k >= 0; k--) {
                                if (matrix[k][i] == matrix[j][i]) {
                                    check = true;
                                    matrix[j][i] += matrix[k][i];
                                    scores += matrix[j][i];
                                    matrix[k][i] = 0;
                                    j = k;
                                    break;
                                } else if (matrix[k][i] != 0) {
                                    break;
                                }
                            }
                        }
                    }
                    for (int j = 6; j >= 0; j--) {
                        if (matrix[j][i] == 0) {
                            for (int k = j - 1; k >= 0; k--) {
                                if (matrix[k][i] != 0) {
                                    check = true;
                                    matrix[j][i] = matrix[k][i];
                                    matrix[k][i] = 0;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            checkForWin();
            if (check == true) randdomnumber();
            gameove();
            setBOX();
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }

    public void gameove() {
        boolean isGameOver = true;

        // Kiểm tra xem có ô trống nào trên bảng không.
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 5; j++) {
                if (matrix[i][j] == 0) {
                    isGameOver = false;
                    Log.d("Test_1", "false_1");
                    break;
                }
            }
            if (!isGameOver) break;
        }

        // Nếu có ô trống, game chưa kết thúc.
        if (!isGameOver) return;

        // Kiểm tra xem có khả năng gộp ô nào không.
        for (int i = 0; i < 6 && isGameOver; i++) {
            for (int j = 0; j < 5 && isGameOver; j++) {
                int currentValue = matrix[i][j];
                if (i < 5 && matrix[i + 1][j] == currentValue) { // Kiểm tra ô dưới
                    isGameOver = false;
                    Log.d("Test_1", "false_2");
                }
                if (j < 4 && matrix[i][j + 1] == currentValue) { // Kiểm tra ô bên phải
                    isGameOver = false;
                    Log.d("Test_1", "false_3");
                }
            }
        }

        // Nếu không thể gộp ô nào, hiện dialog thông báo game over.
        if (isGameOver) {
            Log.d("Test_1", "false_4");
            final Dialog dialog = new Dialog(this);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.item_game_over);
            dialog.show();
            final Button menu = (Button) dialog.findViewById(R.id.menu);
            Button Again = (Button) dialog.findViewById(R.id.Again) ;
            TextView diemso = (TextView) dialog.findViewById(R.id.diemso);
            TextView diemcao = (TextView) dialog.findViewById(R.id.diemcao);
            diemso.setText("New "+String.valueOf(scores));
            diemcao.setText("Best "+String.valueOf(getDiemcao2048()));

            menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Game2048Activity.this, MainActivity.class));
                }
            });
            Again.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    khoitao();
                    dialog.cancel();
                }
            });
            // ... phần code hiện dialog của bạn ở đây
        }
    }
    private void setDiemcao2048(int diemcao) {
        SharedPreferences sharedPref = getSharedPreferences("game2048", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("diemcao", diemcao);
        editor.apply();
    }

    private int getDiemcao2048() {
        SharedPreferences sharedPref = getSharedPreferences("game2048", Context.MODE_PRIVATE);
        return sharedPref.getInt("diemcao", 0);  // 0 là giá trị mặc định
    }
    public void setLuumatrix() {
        for (int i = 1; i < 42; i++) luumatrix[i / 6][i % 6] = matrix[i / 6][i % 6];
    }
}