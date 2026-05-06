package com.example.recyclemarketplace;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.recyclemarketplace.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ✅ Binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // ✅ Toolbar
        setSupportActionBar(binding.appBarMain.toolbar);

        // ✅ FAB
        if (binding.appBarMain.fab != null) {
            binding.appBarMain.fab.setOnClickListener(view ->
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAnchorView(binding.appBarMain.fab)
                            .show()
            );
        }

        // ✅ NavController
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment_content_main);

        if (navHostFragment == null) return;

        NavController navController = navHostFragment.getNavController();

        // ✅ Navigation Drawer
        NavigationView navigationView = binding.navView;
        if (navigationView != null) {
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_transform,
                    R.id.nav_reflow,
                    R.id.nav_slideshow,
                    R.id.nav_settings
            ).setOpenableLayout(binding.drawerLayout).build();

            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);
        }

        // ✅ Bottom Navigation
        BottomNavigationView bottomNav = binding.appBarMain.contentMain.bottomNavView;
        if (bottomNav != null) {
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_transform,
                    R.id.nav_reflow,
                    R.id.nav_slideshow
            ).build();

            NavigationUI.setupWithNavController(bottomNav, navController);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        NavigationView navView = findViewById(R.id.nav_view);

        if (navView == null) {
            getMenuInflater().inflate(R.menu.overflow, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_settings) {
            NavController navController = Navigation.findNavController(
                    this, R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.nav_settings);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(
                this, R.id.nav_host_fragment_content_main);

        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}