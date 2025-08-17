package ru.iskaskad.iskaskadapp

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import ru.iskaskad.iskaskadapp.databinding.ActivityMainBinding
import ru.iskaskad.iskaskadapp.ISKaskadAPP.Companion as GlobalApp

class MainActivity : AppCompatActivity() {
    companion object {
        const val LogTAG = "MainActivity"
    }

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    val startForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        GlobalApp.sendLogMessage(LogTAG, "CAMBARCODE Event in")
        if (result.resultCode == RESULT_OK) {
            val data = result.data!!
            val BarCodeStr = data.getStringExtra("SCAN_RESULT")
            GlobalApp.sendLogMessage(LogTAG, "CAMBARCODE:$BarCodeStr")
            Thread {
                Thread.sleep(200)
                val newintent = Intent(GlobalApp.SCAN_ACTION)
                val barocode = BarCodeStr!!
                val barocodelen = barocode.length
                newintent.putExtra(GlobalApp.BARCODE_NAME, barocode)
                newintent.putExtra(GlobalApp.BARCODE_LENGTH, barocodelen)
                newintent.setPackage(packageName)
                GlobalApp.sendLogMessage(LogTAG, "DelayedSendBarCodeBroadcast:$BarCodeStr")
                sendBroadcast(newintent)
            }.start()
        } else {
            GlobalApp.sendLogMessage(LogTAG, "No BarCode DATA")
        }
        GlobalApp.sendLogMessage(LogTAG, "CAMBARCODE Event out")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GlobalApp.sendLogMessage(LogTAG, "onCreate")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_login, R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_mtask
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        binding.appBarMain.fab.setOnClickListener { view ->


            try {
                GlobalApp.sendLogMessage(LogTAG, "Get barcode from CAM IN")
                val intent = Intent("com.google.zxing.client.android.SCAN")
                startForResult.launch(intent)
                GlobalApp.sendLogMessage(LogTAG, "Start Barcode APP success. Waiting for Barcode Event")
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(applicationContext, "ERROR:$e", Toast.LENGTH_LONG).show()
                GlobalApp.sendLogMessage(LogTAG, "Get barcode from CAM ERROR")
            }
            GlobalApp.sendLogMessage(LogTAG, "Get barcode from CAM OUT")
        }
        binding.appBarMain.fab.setOnLongClickListener { v ->
            v.setOnTouchListener { view, event ->
                when (event.actionMasked) {
                    MotionEvent.ACTION_MOVE -> {
                        view.x = event.rawX - binding.appBarMain.fab.width / 2
                        view.y = event.rawY - binding.appBarMain.fab.height / 2
                    }
                    MotionEvent.ACTION_UP -> view.setOnTouchListener(null)
                    else -> { }
                }
                true
            }
            true
        }

        val appVM: IsKaskadAPPVM by viewModels()

        appVM.RunProgress.observe(this) {
            it?.let {
                if (it <= -1) {
                    binding.appBarMain.progressBar.visibility = View.GONE
                } else {
                    binding.appBarMain.progressBar.progress = it
                    binding.appBarMain.progressBar.visibility = View.VISIBLE
                }
            }
        }

        appVM.ErrorText.observe(this) {
            it?.let {
                if (it == "")
                    binding.appBarMain.TransferError.visibility = View.GONE
                else {
                    binding.appBarMain.TransferError.text = it
                    binding.appBarMain.TransferError.visibility = View.VISIBLE
                }
            }
        }

        appVM.ErrorMessage.observe(this) {
            it?.let {
                if (it != "") {
                    with(AlertDialog.Builder(this)) {
                        setTitle("Произошла ошибка")
                        setMessage(it)
                        show()
                    }
                    appVM.ErrorMessage.postValue("")
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
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