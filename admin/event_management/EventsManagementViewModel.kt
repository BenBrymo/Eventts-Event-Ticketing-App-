

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.eventts.dataClass.Event
import com.example.eventts.dataClass.TicketType
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable

class EventsManagementViewModel : ViewModel() {

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events.asStateFlow()

    private val _draftedEvents = MutableStateFlow<List<Event>>(emptyList())
    val draftedEvents: StateFlow<List<Event>> = _draftedEvents.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _filteredEvents = MutableStateFlow<List<Event>>(emptyList())
    val filteredEvents: StateFlow<List<Event>>  = _filteredEvents.asStateFlow()

    private val _filteredEventsAdvanced = MutableStateFlow<List<Event>>(emptyList())
    val filteredEventsAdvanced: StateFlow<List<Event>>  = _filteredEventsAdvanced.asStateFlow()

    val db = FirebaseFirestore.getInstance()

    init {
        fetchEvents()
    }

    private fun filterEvents(query: String) {
        val filteredList = _events.value.filter { event ->
            event.eventName.contains(query, ignoreCase = true) ||
                    event.eventVenue.contains(query, ignoreCase = true)
        }
        _filteredEvents.value = filteredList
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        filterEvents(query)
    }

    fun filterEventsAdvanced(
        eventStatus: String,
        eventCategories: List<String>,
        eventStartDate: String,
        eventEndDate: String,
        matchAllCriteria: Boolean
    ) {
        // Log the filtering criteria
        Log.d("FilterEvents", "Filter Criteria - Status: $eventStatus, Categories: $eventCategories, Start Date: $eventStartDate, End Date: $eventEndDate, Match All: $matchAllCriteria")

        val filteredListAdvanced = _events.value.filter { event ->
            val matchesStatus = eventStatus.isEmpty() || event.eventStatus == eventStatus
            val matchesCategory = eventCategories.isEmpty() || eventCategories.contains(event.eventCategory)
            val matchesStartDate = eventStartDate.isEmpty() || event.startDate == eventStartDate
            val matchesEndDate = eventEndDate.isEmpty() || event.endDate == eventEndDate

            val matchesAll = matchesStatus && matchesCategory && matchesStartDate && matchesEndDate
            val matchesAny = matchesStatus || matchesCategory || matchesStartDate || matchesEndDate

            if (matchAllCriteria) matchesAll else matchesAny
        }

        // Log the filtered results
        Log.d("FilterEvents", "Filtered Events Count: ${filteredListAdvanced.size}")
        _filteredEventsAdvanced.value = filteredListAdvanced
        Log.d("FilterEvents", "Filtered Events in count in filteredEvent state: ${_filteredEventsAdvanced.value.size}")
    }



    fun fetchEvents() {
        db.collection("events")
            .get()
            .addOnSuccessListener { result ->
                val draftedList = mutableListOf<Event>()
                val nonDraftedList = mutableListOf<Event>()
                for (document in result) {
                    val data = document.data
                    Log.d("FirestoreData", "Document ID: ${document.id}, Data: $data")

                    // Manually map Firestore data to Event class
                    val event = Event(
                        id = document.id,
                        eventName = data["eventName"] as? String ?: "",
                        startDate = data["startDate"] as? String ?: "",
                        endDate = data["endDate"] as? String ?: "",
                        isMultiDayEvent = data["isMultiDayEvent"] as? Boolean ?: false,
                        eventTime = data["eventTime"] as? String ?: "",
                        eventVenue = data["eventVenue"] as? String ?: "",
                        eventStatus = data["eventStatus"] as? String ?: "",
                        eventCategory = data["eventCategory"] as? String ?: "",
                        ticketTypes = (data["ticketTypes"] as? List<Map<String, Any>>)?.map {
                            TicketType(
                                name = it["name"] as? String ?: "",
                                price = (it["price"] as? Number)?.toDouble() ?: 0.0,
                                availableTickets = (it["availableTickets"] as? Number)?.toInt() ?: 0
                            )
                        } ?: listOf(),
                        imageUri = data["imageUri"] as? String ?: "",
                        isDraft = data["isDraft"] as? Boolean ?: false,
                        eventOrganizer = data["eventOrganizer"] as? String ?: "",
                        eventDescription = data["eventDescription"] as? String ?: "",
                    )

                    Log.d("FirestoreEvent", "Event ID: ${event.id}, Is Draft: ${event.isDraft}")

                    if (event.isDraft) {
                        draftedList.add(event)
                    } else {
                        nonDraftedList.add(event)
                    }
                }
                _draftedEvents.value = draftedList
                _events.value = nonDraftedList

                Log.d("EventCount", "Total drafted events: ${draftedList.size}")
                Log.d("EventCount", "Total non-drafted events: ${nonDraftedList.size}")
            }
            .addOnFailureListener { exception ->
                // Handle the error
            }
    }

    fun getEventById(eventId: String?): Event? {
       val eventList = _events.value
       val event =  eventList.find { it.id == eventId }
        event?.let {
            return event
        }?: run {
            val draftedEventList = _draftedEvents.value
            val draftedEvent = draftedEventList.find { it.id == eventId }
            draftedEvent?.let {
                return draftedEvent
            }?: return null
        }
    }

    fun saveEventToFirestore(
        eventName: String,
        startDate: String,
        endDate: String,
        isMultiDayEvent: Boolean,
        eventTime: String,
        eventVenue: String,
        eventStatus: String,
        eventCategory: String,
        ticketTypes: List<TicketType>,
        imageUri: Uri?,
        isDraft: Boolean,
        eventOrganizer: String,
        eventDescription: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val eventData = hashMapOf(
            "eventName" to eventName,
            "startDate" to startDate,
            "endDate" to endDate,
            "isMultiDayEvent" to isMultiDayEvent,
            "eventTime" to eventTime,
            "eventVenue" to eventVenue,
            "eventStatus" to eventStatus,
            "eventCategory" to eventCategory,
            "ticketTypes" to ticketTypes.map { mapOf("name" to it.name, "price" to it.price, "availableTickets" to it.availableTickets) },
            "imageUri" to imageUri?.toString(),
            "isDraft" to isDraft,
            "eventOrganizer" to eventOrganizer,
            "eventDescription" to eventDescription
        )

        db.collection("events")
            .add(eventData)
            .addOnSuccessListener { documentReference ->
                val newEvent = Event(
                    id = documentReference.id,
                    eventName = eventName,
                    startDate = startDate,
                    endDate = endDate,
                    isMultiDayEvent = isMultiDayEvent,
                    eventTime = eventTime,
                    eventVenue = eventVenue,
                    eventStatus = eventStatus,
                    eventCategory = eventCategory,
                    ticketTypes = ticketTypes,
                    imageUri = imageUri?.toString() ?: "",
                    isDraft = isDraft,
                    eventOrganizer = eventOrganizer,
                    eventDescription = eventDescription,

                )
                println("Saving event. ID: ${documentReference.id}, isDraft: ${isDraft}")
                addEvent(newEvent)
                println("Is event draft? ${newEvent.isDraft}")
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }

    fun addEvent(newEvent: Event) {
        if (newEvent.isDraft) {
            _draftedEvents.value += newEvent
            println("Added to drafted events: ${newEvent.eventName}")
        } else {
            _events.value += newEvent
            println("Added to non-drafted events: ${newEvent.eventName}")
        }
        println("Added event. IsDraft: ${newEvent.isDraft}, Total Drafted: ${_draftedEvents.value.size}, Total Non-Drafted: ${_events.value.size}")
    }

    fun deleteEvent(event: Event) {
        db.collection("events").document(event.id).delete()
            .addOnSuccessListener {
                _events.value -= event
            }
            .addOnFailureListener { exception ->
                // Handle the error
            }
    }

    fun copyEvent(event: Event) {
        val newEvent = event.copy(id = "")
        addEvent(newEvent)
    }
}

