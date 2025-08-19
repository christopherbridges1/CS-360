package com.example.chrisbridgesweighttracker3;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

// Adapter for the weight history grid
public class WeightAdapter extends RecyclerView.Adapter<WeightAdapter.ViewHolder> {

    public interface DeleteClickListener {
        void onDeleteClick(int position);
    }

    private final List<WeightEntry> weightEntries;
    private final DeleteClickListener deleteClickListener;

    private static final NumberFormat ONE_DEC_FMT = NumberFormat.getNumberInstance(Locale.US);
    static {
        ONE_DEC_FMT.setMaximumFractionDigits(1);
        ONE_DEC_FMT.setMinimumFractionDigits(1);
    }

    public WeightAdapter(List<WeightEntry> weightEntries, DeleteClickListener deleteClickListener) {
        this.weightEntries = weightEntries;
        this.deleteClickListener = deleteClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.weight_entry, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WeightEntry entry = weightEntries.get(position);

        holder.textDate.setText(entry.getDate());

        // Ensure formatting is correct
        String numericOnly = entry.getWeight().replace("lbs", "").trim();
        String display;
        try {
            double val = Double.parseDouble(numericOnly);
            display = holder.itemView.getContext()
                    .getString(R.string.weight_with_unit, ONE_DEC_FMT.format(val));
        } catch (NumberFormatException e) {
            // Fallback if parsing fails
            display = holder.itemView.getContext()
                    .getString(R.string.weight_with_unit, numericOnly);
        }
        holder.textWeight.setText(display);

        holder.buttonDelete.setOnClickListener(v -> deleteClickListener.onDeleteClick(position));
    }

    @Override
    public int getItemCount() {
        return weightEntries.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textDate, textWeight;
        Button buttonDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textDate = itemView.findViewById(R.id.textDate);
            textWeight = itemView.findViewById(R.id.textWeight);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }
}
