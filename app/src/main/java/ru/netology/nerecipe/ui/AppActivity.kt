package ru.netology.nerecipe.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

import ru.netology.nerecipe.R

class AppActivity : AppCompatActivity(R.layout.app_activity) {

    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        val bnvMain = findViewById<BottomNavigationView>(R.id.bottom_nav)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        bnvMain.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _ , destination , _ ->
            when (destination.label) {
                "fragment_edit_recipe" -> bnvMain.visibility = View.GONE
                "SingleRecipe" -> bnvMain.visibility = View.GONE
                "EditStage" -> bnvMain.visibility = View.GONE
                else -> bnvMain.visibility = View.VISIBLE
            }
        }
    }
}