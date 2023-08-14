package com.caiquocdat.game2048;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.telecom.Call;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.caiquocdat.game2048.databinding.ActivityGame2048Binding;
import com.caiquocdat.game2048.databinding.ActivitySodokuBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SodokuActivity extends AppCompatActivity {
    private ActivitySodokuBinding activitySodokuBinding;
    private Handler handler = new Handler();
    private long seconds = 0;
    TextView[][] sudokuCells = new TextView[9][9];
    private TextView selectedCell = null;
    private int[][] solution;
    private int[][] initialSudokuBoard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySodokuBinding= ActivitySodokuBinding.inflate(getLayoutInflater());
        View view = activitySodokuBinding.getRoot();
        setContentView(view);
        hideSystemUI();
        startTimer();
        setUpData();
        setUpNumberListeners();
        fetchSudokuBoard();

        activitySodokuBinding.backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        activitySodokuBinding.delectImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetSudokuBoardToInitial();
            }
        });
        activitySodokuBinding.againTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activitySodokuBinding.winLinear.setVisibility(View.GONE);
                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {
                        sudokuCells[i][j].setEnabled(true);
                    }
                }
                activitySodokuBinding.backImg.setEnabled(true);
                seconds=0;
                startTimer();
                setUpData();
                setUpNumberListeners();
                fetchSudokuBoard();
            }
        });
    }

    private void resetSudokuBoardToInitial() {
        if (initialSudokuBoard == null) {
            return; // Không làm gì nếu dữ liệu ban đầu chưa sẵn sàng
        }

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (initialSudokuBoard[i][j] != 0) {
                    sudokuCells[i][j].setText(String.valueOf(initialSudokuBoard[i][j]));
                } else {
                    sudokuCells[i][j].setText(""); // hoặc bất kỳ giá trị trống nào bạn muốn đặt
                }
            }
        }
    }

    public void fetchSudokuBoard() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://sudoku-api.vercel.app/api/dosuku")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        Log.d("Test_10", "onResponse: "+jsonObject);
                        // Giả sử bạn muốn lấy mảng "data" từ JSON
                        JSONObject newBoard = jsonObject.getJSONObject("newboard");
                        JSONArray gridsArray = newBoard.getJSONArray("grids");
                        JSONObject firstGrid = gridsArray.getJSONObject(0);
                        JSONArray sudokuData = firstGrid.getJSONArray("value");
                        JSONArray sudokuSolutionData = firstGrid.getJSONArray("solution");
                        // Lúc này, bạn có mảng 2 chiều sudokuBoard, sử dụng nó như bạn muốn.
                        solution = new int[9][9];
                        initialSudokuBoard = new int[9][9];
                        for (int i = 0; i < 9; i++) {
                            for (int j = 0; j < 9; j++) {
                                solution[i][j] = sudokuSolutionData.getJSONArray(i).getInt(j);
                                initialSudokuBoard[i][j] = sudokuData.getJSONArray(i).getInt(j);
                                final int value = sudokuData.getJSONArray(i).getInt(j);
                                Log.d("Test_10", "onResponse: "+value);
                                final TextView currentCell = sudokuCells[i][j];

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (value != 0) { // giả sử 0 là giá trị trống
                                            currentCell.setText(String.valueOf(value));
                                        } else {
                                            currentCell.setText(""); // hoặc bất kỳ giá trị trống nào bạn muốn đặt
                                        }
                                    }
                                });
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void setUpData() {
        for (int i = 1; i <= 9; i++) { // Bắt đầu từ 1 vì bạn đặt ID từ 1
            for (int j = 1; j <= 9; j++) {
                String cellId = "edt_" + i + j;  // Thay đổi cấu trúc ID tại đây
                int resID = getResources().getIdentifier(cellId, "id", getPackageName());
                sudokuCells[i-1][j-1] = findViewById(resID); // Bạn cần trừ đi 1 vì mảng bắt đầu từ 0
                sudokuCells[i-1][j-1].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedCell = (TextView) v;
                        v.setBackgroundResource(R.drawable.sudoku_cell_click);
                        activitySodokuBinding.suggestImg.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                suggestValueForSelectedCell();

                            }
                        });
                    }
                });
            }
        }
    }

    private void suggestValueForSelectedCell() {
        if (selectedCell == null || solution == null) {
            return; // Không làm gì nếu không có ô nào được chọn hoặc giải pháp chưa sẵn sàng
        }

        int row = -1, col = -1;

        // Tìm hàng và cột của ô được chọn
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (sudokuCells[i][j] == selectedCell) {
                    row = i;
                    col = j;
                    break;
                }
            }
        }

        // Cập nhật giá trị cho ô được chọn
        if (row != -1 && col != -1) {
            selectedCell.setText(String.valueOf(solution[row][col]));
            selectedCell.setBackgroundResource(R.drawable.sudoku_cell);
        }
        activitySodokuBinding.undoImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedCell.setText("");
            }
        });
    }

    private void setUpNumberListeners() {
        for (int i = 1; i <= 9; i++) {
            String numberId = "tv_" + i;
            int resID = getResources().getIdentifier(numberId, "id", getPackageName());
            TextView numberTv = findViewById(resID);

            numberTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectedCell != null) {
                        String value = ((TextView) v).getText().toString();
                        selectedCell.setText(((TextView) v).getText());
                        selectedCell.setBackgroundResource(R.drawable.sudoku_cell);
                        checkInputValue(selectedCell, Integer.parseInt(value));
                        if (checkForWin()) {
                            showWinDialog();
                        }
                    }
                }
            });
        }
    }

    private void showWinDialog() {
       activitySodokuBinding.winLinear.setVisibility(View.VISIBLE);
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                sudokuCells[i][j].setEnabled(false);
            }
        }
        activitySodokuBinding.backImg.setEnabled(false);
       activitySodokuBinding.timeOverTv.setText(activitySodokuBinding.timeTv.getText().toString());
        handler.removeCallbacksAndMessages(null);
    }

    private boolean checkForWin() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                String cellValue = sudokuCells[i][j].getText().toString();
                if (cellValue.isEmpty() || Integer.parseInt(cellValue) != solution[i][j]) {
                    return false; // Giá trị của ô không giống với giải pháp
                }
            }
        }
        return true; // Tất cả các giá trị đều giống với giải pháp
    }


    private void checkInputValue(TextView cell, int value) {
        int row = -1, col = -1;

        // Tìm hàng và cột của ô được chọn
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (sudokuCells[i][j] == cell) {
                    row = i;
                    col = j;
                    break;
                }
            }
        }

        // Kiểm tra giá trị với giải pháp
        if (row != -1 && col != -1) {
            if (solution[row][col] == value) {
                Toast.makeText(this, "Chính xác!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Không chính xác!", Toast.LENGTH_SHORT).show();
                activitySodokuBinding.undoImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cell.setText("");
                    }
                });
            }
        }
    }


    private void startTimer() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                seconds++;
                activitySodokuBinding.timeTv.setText(formatTime(seconds));
                handler.postDelayed(this, 1000);
            }
        }, 1000);
    }

    private String formatTime(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;

        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, secs);
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
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}