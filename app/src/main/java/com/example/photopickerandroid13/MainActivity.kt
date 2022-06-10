package com.example.photopickerandroid13

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import coil.compose.AsyncImage
import com.example.photopickerandroid13.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var getContent: ActivityResultLauncher<Intent>
    private val photoIntent = Intent(MediaStore.ACTION_PICK_IMAGES).apply {
        type = "image/*"
        // TODO if set max to 1, failed immediate so provide more than 1
        // todo when request multiple images you need to access accordingly
        putExtra(MediaStore.EXTRA_PICK_IMAGES_MAX, 2)
    }

    val a: Intent = Intent(MediaStore.ACTION_PICK_IMAGES).apply {
        type = "image/*"
        // only videos
        type = "video/*"
    }

    val maxLimit = MediaStore.getPickImagesMaxLimit()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
        getContent =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                result?.let { nonNullableResult ->
                    Log.d(TAG, "onCreate:RESULT_CODE ${nonNullableResult.resultCode}")
                    when (nonNullableResult.resultCode == RESULT_OK) {
                        true -> {
                            val intent = nonNullableResult.data
                            intent?.data?.let { uri ->
                                Log.d(TAG, "onCreate**: $uri")
                                // TODO later
                                /*binding.contentHolder.composeViewImgHolder.setContent {
                                   // PhotoPickerResultComposable(imageUri = uri)
                                }*/
                            }
                        }
                        false -> {
                        }
                    }
                }
            }

        binding.contentHolder.composeViewImgHolder.setContent {
            PhotoPickerResultComposable()
        }

        binding.fab.setOnClickListener {
            Log.d(TAG, "onCreate:${MediaStore.getPickImagesMaxLimit()}")
            val intent = Intent(MediaStore.ACTION_PICK_IMAGES).apply {
                type = "image/*"
                // TODO if set max to 1, failed immediate so provide more than 1
                // todo when request multiple images you need to access accordingly
                //   putExtra(MediaStore.EXTRA_PICK_IMAGES_MAX, 2)
            }
//            startActivityForResult(intent, PHOTO_PICKER_REQUEST_CODE)
            getContent.launch(intent)
        }
    }

/*    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PHOTO_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            Toast.makeText(this, "RESULT $resultCode", Toast.LENGTH_SHORT).show()
        }
    }*/

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) ||
            super.onSupportNavigateUp()
    }

    @Composable
    fun PhotoPickerResultComposable() {
        var result by rememberSaveable { mutableStateOf<Uri?>(null) }
        val launcher =
            rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result = it.data?.data
            }
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Log.d(TAG, "StateLessComposable: **Compose me column**")
            OpenPhotoPicker(openLauncher = { launcher.launch(photoIntent) })
            AsyncImage(
                model = result,
                contentDescription = "Image from photo picker",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(200.dp, 200.dp)
                    .clip(CircleShape)
            )
        }
    }

    @Composable
    fun OpenPhotoPicker(openLauncher: () -> Unit) {
        OutlinedButton(onClick = openLauncher) {
            Log.d(TAG, "StateLessComposable: **Compose me button**")
            Text("Open photo picker")
        }
    }

    @Preview
    @Composable
    fun OpenPickerPreview() {
        setContent {
            MaterialTheme {
                OpenPhotoPicker {}
            }
        }
    }

    /*   @PreviewDevice
       @Composable
       fun BodyContentPreview() {
           setContent {
               PhotoPickerResultComposable()
           }
       }*/

    companion object {
        private const val TAG = "MainActivity"
        const val PHOTO_PICKER_REQUEST_CODE = 101
        const val MAX_NUM_OF_PHOTOS = 15
    }
}
