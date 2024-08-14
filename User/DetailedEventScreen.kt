package com.example.eventts.user


import EventsManagementViewModel

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.eventts.dataClass.Event
import com.example.eventts.dataClass.TicketType
import com.example.eventts.navigation.Routes


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailedEventScreen(
    eventId: String,
    viewModel: EventsManagementViewModel = viewModel(),
    navController: NavController
) {
    val event = viewModel.getEventById(eventId)

    if (event == null) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator() // Display loading indicator
            }
        }
        return
    }

    val context = LocalContext.current


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Event Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { paddingValues ->
        Surface(modifier = Modifier.padding(paddingValues)) {
            DetailedEventContent(
                event = event,
                navController = navController
            )
        }
    }
}

// suspend fun fetchAccessCode(): String? {
//    return withContext(Dispatchers.IO) {
//        val url = "https://api.paystack.co/transaction/initialize"
//        val client = OkHttpClient()
//        val requestBody = FormBody.Builder()
//            .add("email", "benbrymo5@gmail.com") // User email or any other required parameters
//            .add("amount", "50000") // Amount in kobo
//            .build()
//
//        val request = Request.Builder()
//            .url(url)
//            .post(requestBody)
//            .addHeader("Authorization", "Bearer sk_test_9391f4990ba13feeb62f9b0ae25ec9d6dd72ebe1")
//            .build()
//
//        try {
//            val response: Response = client.newCall(request).execute()
//            if (response.isSuccessful) {
//                val responseData = response.body?.string()
//                val jsonObject = responseData?.let { JSONObject(it) }
//                if (jsonObject?.has("data") == true) {
//                    val dataObject = jsonObject.getJSONObject("data")
//                    if (dataObject.has("access_code")) {
//                        dataObject.getString("access_code")
//                    } else {
//                        Log.e("FetchAccessCode", "No value for access_code in the response")
//                        null
//                    }
//                } else {
//                    Log.e("FetchAccessCode", "No data field in the response")
//                    null
//                }
//            } else {
//                Log.e("FetchAccessCode", "Failed to fetch access code. Response code: ${response.code}")
//                null
//            }
//        } catch (e: IOException) {
//            Log.e("FetchAccessCode", "IOException while fetching access code: ${e.message}", e)
//            null
//        } catch (e: JSONException) {
//            Log.e("FetchAccessCode", "JSONException while parsing response: ${e.message}", e)
//            null
//        } catch (e: Exception) {
//            Log.e("FetchAccessCode", "Exception while fetching access code ${e.message}", e)
//            null
//        }
//    }
//}

@Composable
fun DetailedEventContent(event: Event, navController: NavController) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Header Section
            Image(
                painter = rememberAsyncImagePainter(event.imageUri),
                contentDescription = "Event Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
        }
        item {
            Text(
                text = event.eventName,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                fontSize = 24.sp
            )
        }
        item {
            Text(
                text = if (event.isMultiDayEvent) "${event.startDate} - ${event.endDate}" else event.startDate,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        item {
            // Event Time
            Text(
                text = "Venue: ${event.eventTime}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        item {
            // Event Venue
            Text(
                text = "Venue: ${event.eventVenue}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        item {
            // Event Description
            Text(
                text = "Description",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
        item {
            Text(
                text = event.eventDescription,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        item {
            // Ticket Information Section
            Text(
                text = "Ticket Information",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
        item {
            TicketDetailsSection(ticketTypes = event.ticketTypes)
        }
        item {
            // Footer Section
            Text(
                text = "Organizer: ${event.eventOrganizer}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
            // Call-to-Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { navController.navigate(Routes.PAYMENT_SCREEN) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Buy Ticket")
                }
            }
        }
    }
}

@Composable
fun TicketDetailsSection(ticketTypes: List<TicketType>) {

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Ticket Details",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        LazyRow {
            items(ticketTypes) { ticketType ->
                TicketTierItem(ticketType = ticketType)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun TicketTierItem(ticketType: TicketType) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = ticketType.name,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Price: ${ticketType.price}",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Available Tickets: ${ticketType.availableTickets}",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
