
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.eventts.dataClass.Event
import com.example.eventts.navigation.Routes

@Composable
fun UserHomeScreen(navController: NavController) {
    val viewModel: EventsManagementViewModel = viewModel()
    val events by viewModel.events.collectAsState()

    val categories = listOf("Music", "Sports", "Tech", "Arts", "Health")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            // Search Bar
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Search for Event names,venue...") },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .onFocusChanged {
                        if (it.isFocused) {
                            navController.navigate(Routes.USER_SEARCH_SCREEN)
                        }
                    },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Featured Event
            Text(
                text = "Featured Event",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            FeaturedEventCard()

            Spacer(modifier = Modifier.height(25.dp))
        }

        categories.forEach { category ->
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = category,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                val categoryEvents = events.filter { it.eventCategory == category }

                if (categoryEvents.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        categoryEvents.forEach { event ->
                            EventCard(
                                event,
                                onEventClicked = { navController.navigate("detailed_event_screen/${event.id}") }
                            )
                        }
                    }
                } else {
                    Text(text = "No events yet", color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun FeaturedEventCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = rememberAsyncImagePainter("https://media.istockphoto.com/id/2150389695/photo/concertgoer-forms-heart-shape-with-hands-at-beachside-music-festival-crowd-enjoys-live.webp?b=1&s=170667a&w=0&k=20&c=NL4ttHWMvTu8CUJlPlk71OXUwnLqj4ub5eyIr_hEqLE="),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
            )
            Text(
                text = "Featured Event",
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
            )
        }
    }
}

@Composable
fun EventCard(event: Event, onEventClicked: (Event) -> Unit) {
    Card(
        modifier = Modifier
            .padding(end = 8.dp)
            .size(120.dp, 160.dp)
            .clickable { onEventClicked(event) },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)

    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = rememberAsyncImagePainter(event.imageUri),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
            )
            Text(
                text = event.eventName,
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
            )
        }
    }
}
