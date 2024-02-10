package com.example.optimate.businessOwner
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.optimate.loginAndRegister.GlobalUserData
import com.google.firebase.firestore.FirebaseFirestore


private val managerAccessList = listOf(
    "Scheduling",
    "Finances",
    "View Employees",
    "Time-off Requests Approval"
)

private val employeeAccessList = listOf(
    "View Schedule",
    "Clock-in/out",
    "View Payroll",
    "Request Time-off",
    "Add Expense",
)

data class TitleEntry(
    val bid: String,
    val role: String,
    val title: String,
    val access: List<String>
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SetupRoleUI(
    role: String, // "Manager" or "Employee"
    onAddComplete: () -> Unit
) {
    val context = LocalContext.current
    var titleText by remember { mutableStateOf("") }
    val selectedAccess = remember { mutableStateListOf<String>() }

    Scaffold(
        topBar = { XmlTopBar(titleText = "Add $role".replaceFirstChar { it.uppercase() }) },
        content = { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TitleInput(titleText,{ titleText = it },modifier = Modifier.weight(2f))
                    Spacer(modifier = Modifier.width(8.dp))
                    AddButton(
                        onClick = {
                            addTitleToDB(
                                bid = GlobalUserData.bid,
                                role = role,
                                title = titleText,
                                access = selectedAccess.toList(),
                                context = context,
                                onSuccess = onAddComplete,
                                onFailure = { e ->
                                    Toast.makeText(context, "Failed to add title: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                                }
                            )
                        },
                        context = context,
                        titleText = titleText,
                        modifier = Modifier.weight(1f)
                    )
                }

                LazyColumn(
                    modifier = Modifier.padding(top = 8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    if (role == "Manager") {
                        stickyHeader {
                            Text(
                                "Manager's Access:",
                                style = MaterialTheme.typography.h6,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colors.background)
                                    .padding(8.dp)
                            )
                        }
                        itemsIndexed(managerAccessList) { index, access ->
                            AccessCardItem(access, index) { accessName, isSelected ->
                                if (isSelected) selectedAccess.add(accessName) else selectedAccess.remove(accessName)
                            }
                        }
                    }

                    stickyHeader {
                        Text(
                            "Employee's Access:",
                            style = MaterialTheme.typography.h6,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colors.background)
                                .padding(8.dp)
                        )
                    }
                    itemsIndexed(employeeAccessList) { index, access ->
                        AccessCardItem(access, index) { accessName, isSelected ->
                            if (isSelected) selectedAccess.add(accessName) else selectedAccess.remove(accessName)
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun TitleInput(titleText: String, onTitleChange: (String) -> Unit, modifier: Modifier = Modifier) {
    var text by remember { mutableStateOf(titleText) }

    OutlinedTextField(
        value = text,
        onValueChange = {
            text = it
            onTitleChange(it)
        },
        label = { Text("Title") },
        textStyle = TextStyle(fontSize = 18.sp),
        modifier = modifier
    )
}

@Composable
fun AddButton(onClick: () -> Unit, context: Context, titleText: String, modifier: Modifier = Modifier) {
    val buttonColor = MaterialTheme.colors.run { if (isLight) Color(0xFFC4F0E6) else Color(0xFF91C9B7) }

    Button(
        colors = ButtonDefaults.buttonColors(backgroundColor = buttonColor),
        onClick = {
            // Check if titleText is not empty before proceeding
            if (titleText.isNotBlank()) {
                onClick()
            } else {
                // Show a Toast message if titleText is empty
                Toast.makeText(context, "Title cannot be empty", Toast.LENGTH_SHORT).show()
            }
        },
        modifier = modifier,
        elevation = ButtonDefaults.elevation(defaultElevation = 8.dp, pressedElevation = 16.dp)
    ) {
        Text("Add", fontSize = 18.sp)
    }
}


@Composable
fun AccessCardItem(access: String, index: Int, onAccessSelected: (String, Boolean) -> Unit) {
    val backgroundColor = if (index % 2 == 0) Color(0xFFC0C2EC) else Color(0xFFF2EBF3)
    var switchState by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = 2.dp,
        backgroundColor = backgroundColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = access, fontSize = 18.sp)
            Switch(
                checked = switchState,
                onCheckedChange = {
                    switchState = it
                    onAccessSelected(access, it)
                }
            )
        }
    }
}

fun addTitleToDB(
    bid: String,
    role: String,
    title: String,
    access: List<String>,
    context: Context, // Pass the context from the activity or use LocalContext.current in a composable
    onSuccess: () -> Unit,
    onFailure: (Exception) -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    // Query to check if the title already exists for the given BID
    db.collection("titles")
        .whereEqualTo("bid", bid)
        .whereEqualTo("title", title)
        .get()
        .addOnSuccessListener { documents ->
            if (documents.size() > 0) {
                // The title already exists for this BID
                Toast.makeText(context, "Title already exists", Toast.LENGTH_SHORT).show()
            } else {
                // The title does not exist, proceed to add it
                val newTitle = TitleEntry(bid, role, title, access)
                db.collection("titles")
                    .add(newTitle)
                    .addOnSuccessListener {
                        onSuccess()
                    }
                    .addOnFailureListener { e ->
                        onFailure(e)
                    }
            }
        }
        .addOnFailureListener { e ->
            // Handle the error, e.g., show an error message
            Toast.makeText(context, "Error checking title: ${e.message}", Toast.LENGTH_SHORT).show()
        }
}





