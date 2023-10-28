package co.edu.udea.compumovil.gr13_20232.generadordeimagenes;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends ComponentActivity{

    EditText mEditTextPromp;
    Button mButtomOk;

    TextView mTextViewRespuesta;
    ImageView mImageViewIaImagen;
    ProgressDialog mProgressDialog;

    public static final MediaType JSON = MediaType.get("application/json");

    OkHttpClient client = new OkHttpClient();

    @Override
     protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditTextPromp = findViewById(R.id.et_promp);
        mButtomOk = findViewById(R.id.btn_ok);
        mTextViewRespuesta = findViewById(R.id.tv_respuesta);
        mImageViewIaImagen = findViewById(R.id.img_iaImagen);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Generando ...");



        mButtomOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String promp = mEditTextPromp.getText().toString().trim();
                if(promp.isEmpty()){
                    mTextViewRespuesta.setText("Ingrese el texto");
                }else{
                    llamarAPI(promp);
                }
            }
        });
    }

    private void llamarAPI(String promp) {
        mProgressDialog.show();
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("prompt", promp);
            jsonBody.put("size", "256x256");
        } catch (Exception e){
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(jsonBody.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/images/generations")
                .header("Authorization", "Bearer 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")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                mTextViewRespuesta.setText("Error al generar la imagen " + e.getMessage());
                mProgressDialog.dismiss();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String imageUrl = jsonObject.getJSONArray("data").getJSONObject(0).getString("url");
                    cargarImagen(imageUrl);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void cargarImagen(String imageUrl) {
        runOnUiThread(() ->{
            Picasso.get().load(imageUrl).into(mImageViewIaImagen);
            mEditTextPromp.setText("");
            mTextViewRespuesta.setText("Exito al generar");
            mProgressDialog.dismiss();

        });
    }

}

