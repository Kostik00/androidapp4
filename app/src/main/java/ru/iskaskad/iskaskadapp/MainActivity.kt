package ru.iskaskad.iskaskadapp

import android.app.AlertDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import ru.iskaskad.iskaskadapp.databinding.ActivityMainBinding
import ru.iskaskad.iskaskadapp.ISKaskadAPP.Companion as GlobalApp

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        binding.appBarMain.fab.setOnClickListener { view ->
            navController.navigate(R.id.nav_settings)
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null)
//                .setAnchorView(R.id.fab).show()
        }


        val AppVM: IsKaskadAPPVM by viewModels()


        AppVM.RunProgress.observe(this, {it?.let {
            if (it <= -1)
            {
                binding.appBarMain.progressBar.visibility = View.GONE
            }
            else {
                binding.appBarMain.progressBar.progress = it
                binding.appBarMain.progressBar.visibility = View.VISIBLE
            }
        }
        })

        AppVM.ErrorText.observe(this, {
            it?.let {
                if (it == "")
                    binding.appBarMain.TransferError.visibility = View.GONE
                else
                {
                    binding.appBarMain.TransferError.text = it
                    binding.appBarMain.TransferError.visibility = View.VISIBLE
                }
            }

        })
        AppVM.ErrorMessage.observe(this, {
            it?.let {
                if (it != "")
                {
                    with(AlertDialog.Builder(this)) {
                        setTitle("Произошла ошибка")
                        setMessage(it)
                        show()
                    }
                    AppVM.ErrorMessage.postValue("")
                }
            }

        })




    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                val navController = findNavController(R.id.nav_host_fragment_content_main)
                navController.navigate(R.id.nav_settings)

                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}