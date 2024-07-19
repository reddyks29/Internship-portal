package com.example.internship2;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class CompanyPageAdapter extends FragmentStateAdapter {

    public CompanyPageAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new CompanyProfileFragment();
            case 1:
                return new CompanyOpeningFragment();
            case 2:
                return new CompanyApplicantsFragment();
            default:
                return new CompanyProfileFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
