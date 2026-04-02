package com.grandwolf.security

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.grandwolf.security.ui.theme.GrandWolfSecurityTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

private const val CONTACT_PHONE = "+201000000000"
private const val CONTACT_EMAIL = "info@grandwolfeg.com"
private const val LOGO_URL = "https://grandwolfeg.com/wp-content/uploads/2026/03/Grand-wolf-Final-2048x861.png"

// Optional: set your backend endpoint to receive service requests as JSON.
// Example: https://api.yourdomain.com/service-requests
private const val BACKEND_ENDPOINT = ""

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GrandWolfSecurityTheme {
                GrandWolfApp()
            }
        }
    }
}

data class ServiceItem(val title: String, val description: String)
data class ServiceRequest(
    val fullName: String,
    val phone: String,
    val email: String,
    val company: String,
    val serviceNeeded: String,
    val details: String
)

private val services = listOf(
    ServiceItem("Land Security & Guarding", "Integrated security programs combining trained personnel, procedures, and technology."),
    ServiceItem("Hotel & Tourism Security", "Specialized protection for hotels and tourist sites with strict confidentiality."),
    ServiceItem("Administrative Office Security", "Access management for employees and visitors with full movement logging."),
    ServiceItem("Private Guards & Close Protection", "VIP and high-profile close protection in normal and high-risk environments."),
    ServiceItem("Tracking & Monitoring", "Professional tracking systems for people and vehicles with setup and training."),
    ServiceItem("Factory & Warehouse Security", "Control of materials, vehicle movement, and documentation to reduce theft risks."),
    ServiceItem("Embassies & Diplomatic Missions", "Discreet, high-safety protection plans for diplomatic facilities and residences."),
    ServiceItem("Private Yacht Security", "Tailored yacht security for owners, guests, and crew during cruise operations."),
    ServiceItem("Conference & Event Security", "End-to-end event security, crowd control, and organizer coordination."),
    ServiceItem("Security Operations Training", "Practical training tailored to each site and client-specific requirements."),
    ServiceItem("Oil & Gas Exploration Security", "Onshore and offshore protection for rigs, platforms, docks, and critical assets.")
)

@Composable
private fun GrandWolfApp() {
    val context = LocalContext.current
    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Grand Wolf Security & Guarding") }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackBarHostState) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item { HeaderCard() }

            item {
                StatementCard(
                    title = "Mission",
                    body = "To become a leading security and guarding company in Egypt and the Middle East through exceptional service and continuous improvement."
                )
            }

            item {
                StatementCard(
                    title = "Vision",
                    body = "To build long-term strategic partnerships through transparency, high-quality performance, and effective communication."
                )
            }

            item {
                StatementCard(
                    title = "Policy",
                    body = "Build client trust, deliver superior value, support client success, and uphold credibility, flexibility, and secure operations."
                )
            }

            item {
                Text(
                    text = "Our Services",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            items(services) { service -> ServiceCard(service = service) }

            item {
                ContactCard(
                    onCall = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$CONTACT_PHONE"))
                        context.startActivity(intent)
                    },
                    onEmail = {
                        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$CONTACT_EMAIL"))
                        context.startActivity(intent)
                    },
                    onWhatsApp = {
                        val defaultMessage = "Hello Grand Wolf, I need details about your security services."
                        val encodedMessage = URLEncoder.encode(defaultMessage, "UTF-8")
                        val phoneForWhatsApp = CONTACT_PHONE.replace("+", "")
                        val uri = Uri.parse("https://wa.me/$phoneForWhatsApp?text=$encodedMessage")
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        context.startActivity(intent)
                    }
                )
            }

            item {
                ServiceRequestCard(
                    onSubmit = { request ->
                        val emailBody = buildEmailBody(request)
                        sendServiceRequestEmail(context, request, emailBody)

                        scope.launch {
                            val result = if (BACKEND_ENDPOINT.isNotBlank()) {
                                sendServiceRequestToBackend(request)
                            } else {
                                "Request opened in email."
                            }
                            snackBarHostState.showSnackbar(result)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun HeaderCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0E1A2A)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(
                        brush = Brush.horizontalGradient(listOf(Color(0xFF0E1A2A), Color(0xFF1C2E45))),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = LOGO_URL,
                    contentDescription = "Grand Wolf logo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Professional protection for people, facilities, and operations.",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Grand Wolf delivers disciplined guarding, operational planning, and reliable execution across commercial, industrial, and event sectors.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFF4F4F4)
            )
        }
    }
}

@Composable
private fun StatementCard(title: String, body: String) {
    Card(shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Text(body, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun ServiceCard(service: ServiceItem) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFD))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = service.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = service.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF2E2E2E)
            )
        }
    }
}

@Composable
private fun ContactCard(onCall: () -> Unit, onEmail: () -> Unit, onWhatsApp: () -> Unit) {
    Card(shape = RoundedCornerShape(18.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Contact Us", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Text("Need a service? Reach us instantly by phone, email, or WhatsApp.")
            Spacer(modifier = Modifier.height(12.dp))
            Divider()
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AssistChip(
                    onClick = onCall,
                    label = { Text("Call") },
                    leadingIcon = {
                        androidx.compose.material3.Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(containerColor = Color(0xFFE8F4FF))
                )

                AssistChip(
                    onClick = onEmail,
                    label = { Text("Email") },
                    leadingIcon = {
                        androidx.compose.material3.Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )

                AssistChip(onClick = onWhatsApp, label = { Text("WhatsApp") })
            }
        }
    }
}

@Composable
private fun ServiceRequestCard(onSubmit: (ServiceRequest) -> Unit) {
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var company by remember { mutableStateOf("") }
    var serviceNeeded by remember { mutableStateOf("") }
    var details by remember { mutableStateOf("") }

    Card(shape = RoundedCornerShape(18.dp)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Service Request", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("Send your request directly to info@grandwolfeg.com and optionally to your backend endpoint.")

            OutlinedTextField(value = fullName, onValueChange = { fullName = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = company, onValueChange = { company = it }, label = { Text("Company") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = serviceNeeded, onValueChange = { serviceNeeded = it }, label = { Text("Service Needed") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(
                value = details,
                onValueChange = { details = it },
                label = { Text("Request Details") },
                modifier = Modifier.fillMaxWidth().height(130.dp)
            )

            Button(
                onClick = {
                    onSubmit(
                        ServiceRequest(
                            fullName = fullName.trim(),
                            phone = phone.trim(),
                            email = email.trim(),
                            company = company.trim(),
                            serviceNeeded = serviceNeeded.trim(),
                            details = details.trim()
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Send Request")
            }
        }
    }
}

private fun sendServiceRequestEmail(context: android.content.Context, request: ServiceRequest, body: String) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:$CONTACT_EMAIL")
        putExtra(Intent.EXTRA_SUBJECT, "Service Request - ${request.serviceNeeded.ifBlank { "General Inquiry" }}")
        putExtra(Intent.EXTRA_TEXT, body)
    }
    context.startActivity(intent)
}

private suspend fun sendServiceRequestToBackend(request: ServiceRequest): String = withContext(Dispatchers.IO) {
    return@withContext try {
        val connection = URL(BACKEND_ENDPOINT).openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "application/json")
        connection.connectTimeout = 15000
        connection.readTimeout = 15000
        connection.doOutput = true

        val json = JSONObject().apply {
            put("fullName", request.fullName)
            put("phone", request.phone)
            put("email", request.email)
            put("company", request.company)
            put("serviceNeeded", request.serviceNeeded)
            put("details", request.details)
        }

        connection.outputStream.use { output ->
            output.write(json.toString().toByteArray())
        }

        if (connection.responseCode in 200..299) {
            "Request submitted successfully."
        } else {
            "Email prepared. Backend returned status ${connection.responseCode}."
        }
    } catch (_: Exception) {
        "Email prepared. Backend submission failed."
    }
}

private fun buildEmailBody(request: ServiceRequest): String {
    return """
        New service request from Grand Wolf app:

        Full name: ${request.fullName}
        Phone: ${request.phone}
        Email: ${request.email}
        Company: ${request.company}
        Service needed: ${request.serviceNeeded}

        Details:
        ${request.details}
    """.trimIndent()
}
