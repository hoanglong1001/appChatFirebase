package com.example.techasians_appchat.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.techasians_appchat.R;
import com.example.techasians_appchat.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.makeramen.roundedimageview.RoundedImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AboutFragment extends Fragment implements View.OnClickListener {

    private RoundedImageView mImgAvatar;
    private TextView mTxtTen;
    private EditText mEdtHoten;
    private EditText mEdtDiachi;
    private TextView mEdtNgaysinh;
    private EditText mEdtCmt;
    private ImageView mIvEditHoten;
    private ImageView mIvEditNgaysinh;
    private ImageView mIvEditDiachi;
    private ImageView mIvEditCmt;
    private Button mBtnUpdate;
    private Button mBtnEdit;
    private User userCurrent;

    public static AboutFragment newInstant(User user) {
        AboutFragment aboutFragment = new AboutFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);
        aboutFragment.setArguments(bundle);
        return aboutFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        mImgAvatar = view.findViewById(R.id.img_user_about);
        mTxtTen = view.findViewById(R.id.name_user_about);
        mEdtHoten = view.findViewById(R.id.edt_hoten);
        mEdtDiachi = view.findViewById(R.id.edt_diachi);
        mEdtNgaysinh = view.findViewById(R.id.edt_ngaysinh);
        mEdtCmt = view.findViewById(R.id.edt_cmt);
        mIvEditHoten = view.findViewById(R.id.iv_edit_hoten);
        mIvEditNgaysinh = view.findViewById(R.id.iv_edit_ngaysinh);
        mIvEditDiachi = view.findViewById(R.id.iv_edit_diachi);
        mIvEditCmt = view.findViewById(R.id.iv_edit_cmt);
        mBtnUpdate = view.findViewById(R.id.btn_update);
        mBtnEdit = view.findViewById(R.id.btn_edit);

        showUserAbout();
        mBtnUpdate.setOnClickListener(this);
        mBtnEdit.setOnClickListener(this);
        mEdtNgaysinh.setOnClickListener(this);
        mEdtCmt.setOnClickListener(this);

        return view;
    }

    private void showUserAbout() {
        userCurrent = (User) getArguments().getSerializable("user");
        if (userCurrent != null) {
            mTxtTen.setText(userCurrent.getName());
            Glide.with(AboutFragment.this).load(userCurrent.getAvatar())
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(mImgAvatar);
            mEdtHoten.setText(userCurrent.getName());
            mEdtDiachi.setText(userCurrent.getDiachi());
            mEdtNgaysinh.setText(userCurrent.getNgaysinh());
            mEdtCmt.setText(userCurrent.getCmt());
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_edit) {
            mEdtHoten.setEnabled(true);
            mEdtHoten.setAlpha(1);
            mIvEditHoten.setVisibility(View.VISIBLE);
            mEdtDiachi.setEnabled(true);
            mEdtDiachi.setAlpha(1);
            mIvEditDiachi.setVisibility(View.VISIBLE);
            mEdtNgaysinh.setEnabled(true);
            mEdtNgaysinh.setAlpha(1);
            mIvEditNgaysinh.setVisibility(View.VISIBLE);
            mEdtCmt.setEnabled(true);
            mEdtCmt.setAlpha(1);
            mIvEditCmt.setVisibility(View.VISIBLE);
            mBtnUpdate.setVisibility(View.VISIBLE);
        }
        if (view.getId() == R.id.edt_ngaysinh) {
            final Calendar calendar = Calendar.getInstance();
            int ngay = calendar.get(Calendar.DATE);
            int thang = calendar.get(Calendar.MONTH);
            int nam = calendar.get(Calendar.YEAR);
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                    calendar.set(i, i1, i2);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    mEdtNgaysinh.setText(simpleDateFormat.format(calendar.getTime()));
                }
            }, nam, thang, ngay);
            datePickerDialog.show();
        }
        if (view.getId() == R.id.btn_update) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

            userCurrent.setName(mEdtHoten.getText().toString());
            userCurrent.setDiachi(mEdtDiachi.getText().toString());
            userCurrent.setNgaysinh(mEdtNgaysinh.getText().toString());
            userCurrent.setCmt(mEdtCmt.getText().toString());

            Map<String, Object> map = new HashMap<>();
            map.put(userCurrent.getId(), userCurrent);

            reference.child("users").updateChildren(map)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getActivity(), "Update success", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), "Exception: " + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
            mEdtHoten.setEnabled(false);
            mEdtHoten.setAlpha(0.2f);
            mIvEditHoten.setVisibility(View.INVISIBLE);
            mEdtDiachi.setEnabled(false);
            mEdtDiachi.setAlpha(0.2f);
            mIvEditDiachi.setVisibility(View.INVISIBLE);
            mEdtNgaysinh.setEnabled(false);
            mEdtNgaysinh.setAlpha(0.2f);
            mIvEditNgaysinh.setVisibility(View.INVISIBLE);
            mEdtCmt.setEnabled(false);
            mEdtCmt.setAlpha(0.2f);
            mIvEditCmt.setVisibility(View.INVISIBLE);
            mBtnUpdate.setEnabled(false);
        }
    }
}
