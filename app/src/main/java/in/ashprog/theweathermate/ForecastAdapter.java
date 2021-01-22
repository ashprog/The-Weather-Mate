package in.ashprog.theweathermate;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import java.util.List;

import in.ashprog.theweathermate.ForecastModel.Hour;

class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder> {

    Context context;
    List<Hour> hourList;

    ForecastAdapter(Context context, List<Hour> hourList) {
        this.context = context;
        this.hourList = hourList;
    }

    @NonNull
    @Override
    public ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_view_layout, parent, false);

        return new ForecastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ForecastViewHolder holder, int position) {
        holder.timeTV.setText(hourList.get(position).getTime().split(" ")[1]);
        holder.tempTV.setText(hourList.get(position).getTempC().toString() + "°");
        holder.condTV.setText(hourList.get(position).getCondition().getText());

        ImageRequest request = new ImageRequest("http:" + hourList.get(position).getCondition().getIcon(),
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        holder.iconIV.setImageBitmap(bitmap);
                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
    }

    @Override
    public int getItemCount() {
        return hourList.size() / 2;
    }

    class ForecastViewHolder extends RecyclerView.ViewHolder {

        ImageView iconIV;
        TextView tempTV, timeTV, condTV;

        public ForecastViewHolder(@NonNull View itemView) {
            super(itemView);

            iconIV = itemView.findViewById(R.id.iconIV);
            tempTV = itemView.findViewById(R.id.tempTV);
            timeTV = itemView.findViewById(R.id.timeTV);
            condTV = itemView.findViewById(R.id.condTV);
        }
    }
}
