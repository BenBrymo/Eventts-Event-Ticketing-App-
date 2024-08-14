package com.example.eventts.admin.main_screen


import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.eventts.MainActivityViewModel
import com.example.eventts.common.SharedPreferencesViewModel
import com.example.eventts.navigation.Routes
import com.example.eventts.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProfileScreen(
    navController: NavController,
    sharedPreferencesViewModel: SharedPreferencesViewModel = viewModel(),
    mainActivityViewModel: MainActivityViewModel = viewModel(),
    viewModel: AdminViewModel = viewModel()
) {
    // Shared preferences data
    val userData by sharedPreferencesViewModel.userInfo.collectAsState()

    // Retrieves profile picture from userData
    var profilePictureUrl by remember { mutableStateOf(userData["PROFILE_PICTURE_URL"]?.toUri()) }
    Log.d("SharedPreference retrived profile url:","$userData[PROFILE_PICTURE_URL]")

    //header image uri variable
    var headerPictureUrl by remember { mutableStateOf(userData["HEADER_PICTURE_URL"]?.toUri()) }
    Log.d("SharedPreference retrived header url:","$userData[HEADER_PICTURE_URL]")


    // Retrieves username from userData
    val username = userData["USERNAME"]

    val pic = "https://plus.unsplash.com/premium_photo-1681830630610-9f26c9729b75?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MXx8Y29uY2VydHxlbnwwfHwwfHx8MA%3D%3D"

    // profile image launcher
    val profileImagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri.let { imageUrl ->
            //sets profilePictureUrl
            profilePictureUrl = imageUrl

            //Update profilePictureUrl in User's SharedPreferences
            viewModel.updateProfileImageUrlInSharedPreferences(imageUrl.toString())

            Log.d("SharedPreference new profile url:","$userData[PROFILE_PICTURE_URL]")

        }
    }


    // header image launcher
    val headerImagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri.let { imageUrl ->
            //sets headerPictureUrl
            headerPictureUrl = imageUrl

            //Update headerPictureUrl in User's SharedPreferences
            viewModel.updateHeaderInSharedPreferences(imageUrl.toString())

            Log.d("SharedPreference new header url:","$userData[HEADER_PICTURE_URL]")
        }
    }

    @Composable
    fun loadImageFromUri(uri: Uri?) {
        val context = LocalContext.current

        val imageLoader = remember { ImageLoader.Builder(context).build() }

        AndroidView(
            factory = {
                ImageView(it).apply {
                    // Optional: Set a placeholder or error image
                    setImageResource(R.drawable.user_logo)
                }
                      },
            update = { imageView ->
                uri?.let {
                    val request = ImageRequest.Builder(context)
                        .data(uri)
                        .target(imageView)
                        .build()
                    imageLoader.enqueue(request)
                }
            }
        )

    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        //Box to keep all content
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            // Card to keep content and for ui design
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                //Box for header and profile image
                Box {
                    //Header image
                    Image(
                        painter = rememberAsyncImagePainter(model = headerPictureUrl),
                        contentDescription = "Header Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clickable {
                                // Launch image picker
                                headerImagePickerLauncher.launch("image/*")
                            },
                        contentScale = ContentScale.Crop
                    )

                    // Profile image Box
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(bottom = 20.dp)
                    ) {

                        // Displays profile image if it exists
                        if (profilePictureUrl != null) {
                            Log.d("ProfilePicture", "Loading image from $profilePictureUrl")
                            Image(
                                painter = rememberAsyncImagePainter(model = profilePictureUrl),
                                contentDescription = "Admin Avatar",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surface)
                                    .clickable {
                                        // Launch image picker
                                        profileImagePickerLauncher.launch("image/*")
                                    },
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Admin Avatar",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surface)
                                    .clickable {
                                        // Launch image picker
                                        profileImagePickerLauncher.launch("image/*")
                                    }
                            )
                        }
                    }
                }
            }

            // Name and Admin Badge Row
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp, top = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = username ?: "Username",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 8.dp),
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(2.dp))

                Badge { Text("Admin") } // Displays badge
            }
        }

        // Scrollable Menu Items
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Event menu item
            item {
                MenuItem(
                    icon = Icons.Default.Event,
                    title = "Drafted Events",
                    // Navigates to Drafted events screen
                    onClick = { navController.navigate(Routes.DRAFTED_EVENTS_SCREEN) }
                )
            }
            // History menu item
            item {
                MenuItem(
                    icon = Icons.Default.History,
                    title = "History",
                    onClick = { /* Navigate to History */ }
                )
            }
            // Statistics menu item
            item {
                MenuItem(
                    icon = Icons.Default.Analytics,
                    title = "Statistics",
                    onClick = { /* Navigate to Statistics */ }
                )
            }
            // Log out menu item
            item {
                MenuItem(
                    icon = Icons.AutoMirrored.Filled.Logout,
                    title = "Log Out",
                    // Performs log out operation
                    onClick = {
                        mainActivityViewModel.signOut(
                            onSuccess = { navController.navigate(Routes.LOGIN_SCREEN) },
                            onError = { Log.d("Log Out AdminProfile Screen ", "Could not log out") }
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun MenuItem(icon: ImageVector, title: String, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Icon(imageVector = icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, style = MaterialTheme.typography.bodyLarge)
    }
}

@Preview
@Composable
fun AdminProfileScreenPreview() {
    AdminProfileScreen(navController = rememberNavController())
}
