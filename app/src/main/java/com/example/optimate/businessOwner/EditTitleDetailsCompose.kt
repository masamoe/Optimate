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
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.optimate.R
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EditTitleUI(
    role: String, // "Manager" or "Employee"
    onAddComplete: () -> Unit,
    titleName: String
) {
    val context = LocalContext.current
    var titleText by remember { mutableStateOf(titleName) }
    val selectedAccess = remember { mutableStateListOf<String>() }
    var isLoading by remember { mutableStateOf(true) }
    val itemId = remember { mutableStateOf<String?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(titleName) {
        isLoading = true // Start loading
        fetchAccess(
            titleName = titleName,
            selectedAccess = selectedAccess,
            onAccessRightsFetched = {
                isLoading = false // Data fetched, stop loading
            },
            itemIdHolder = itemId,
            context = context
        )
    }
    if (showDeleteDialog && itemId.value != null) {
        ConfirmDeleteDialog(
            onConfirm = {
                deleteItemFromDB(itemId.value!!, context) {
                    onAddComplete()
                }
                showDeleteDialog = false
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    if (isLoading) {
        // Show loading UI
        CircularProgressIndicator() // Adjust according to your design
    } else {
        Scaffold(
            topBar = { XmlTopBar(titleText = "Edit $role") },
            content = { innerPadding ->
                Column(modifier = Modifier.padding(innerPadding)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TitleField(
                            initialValue = titleName,
                            onTitleChange = { titleText = it },
                            modifier = Modifier.weight(2f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        if (itemId.value != null) {
                            DeleteButton { showDeleteDialog = true }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        SaveButton(
                            onClick = {
                                updateTitleInDB(
                                    itemId = itemId.value ?: "",
                                    title = titleText,
                                    access = selectedAccess,
                                    context = context,
                                    onSuccess = onAddComplete,
                                    onFailure = { e ->
                                        Toast.makeText(context, "Failed to add title: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                                    }
                                )

                            },
                            context = context,
                            titleText = titleText,
                            initialValue = titleName,
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
                                AccessItem(access, index, selectedAccess) { accessName, isSelected ->
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
                            AccessItem(access, index, selectedAccess) { accessName, isSelected ->
                                if (isSelected) selectedAccess.add(accessName) else selectedAccess.remove(accessName)
                            }
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun TitleField(initialValue: String, onTitleChange: (String) -> Unit, modifier: Modifier = Modifier) {
    var text by remember { mutableStateOf(initialValue) }

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
fun DeleteButton(
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(
            painter = painterResource(id = R.drawable.delete_icon),
            contentDescription = "Delete"
        )
    }
}

@Composable
fun ConfirmDeleteDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Title") },
        text = { Text("Are you sure you want to delete this title?") },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Delete")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("No, Go Back")
            }
        }
    )
}

@Composable
fun SaveButton(
    onClick: () -> Unit,
    context: Context,
    titleText: String,
    initialValue: String,
    modifier: Modifier = Modifier
) {
    val buttonColor = MaterialTheme.colors.run { if (isLight) Color(0xFFC4F0E6) else Color(0xFF91C9B7) }

    Button(
        colors = ButtonDefaults.buttonColors(backgroundColor = buttonColor),
        onClick = {
            when {
                titleText.isBlank() -> {
                    Toast.makeText(context, "Title cannot be empty", Toast.LENGTH_SHORT).show()
                }
                titleText == initialValue -> {
                    onClick() // No changes made, proceed as success
                }
                else -> {
                    // Changes made and title is not blank, proceed with update
                    onClick()
                }
            }
        },
        modifier = modifier,
        elevation = ButtonDefaults.elevation(defaultElevation = 8.dp, pressedElevation = 16.dp)
    ) {
        Text("Save", fontSize = 15.sp)
    }
}



@Composable
fun AccessItem(
    access: String,
    index: Int,
    selectedAccess: MutableList<String>, // Pass the list of selected accesses
    onAccessSelected: (String, Boolean) -> Unit
) {
    val backgroundColor = if (index % 2 == 0) Color(0xFFC0C2EC) else Color(0xFFF2EBF3)
    var switchState by remember { mutableStateOf(access in selectedAccess) } // Check if the switch should be on

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


fun fetchAccess(
    titleName: String,
    selectedAccess: MutableList<String>,
    onAccessRightsFetched: () -> Unit,
    itemIdHolder: MutableState<String?>, // Add this parameter to hold the item ID
    context: Context
) {
    val db = FirebaseFirestore.getInstance()
    db.collection("titles")
        .whereEqualTo("bid", GlobalUserData.bid)
        .whereEqualTo("title", titleName)
        .get()
        .addOnSuccessListener { documents ->
            var isFirstItem = true
            for (document in documents) {
                if (isFirstItem) {
                    itemIdHolder.value = document.id // Save the first document's ID
                    isFirstItem = false
                }
                val accessList = document.get("access")
                if (accessList is List<*>) {
                    selectedAccess.clear()
                    selectedAccess.addAll(accessList.filterIsInstance<String>())
                }
            }
            onAccessRightsFetched()
        }
        .addOnFailureListener { exception ->
            Toast.makeText(context, "Error fetching access rights: ${exception.localizedMessage}", Toast.LENGTH_LONG).show()
        }
}

fun updateTitleInDB(
    itemId: String,
    title: String,
    access: List<String>,
    context: Context,
    onSuccess: () -> Unit,
    onFailure: (Exception) -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val titleDocument = db.collection("titles").document(itemId)

    titleDocument.update(mapOf(
        "title" to title,
        "access" to access
    )).addOnSuccessListener {
        onSuccess()
    }.addOnFailureListener { exception ->
        onFailure(exception)
    }
}

fun deleteItemFromDB(itemId: String, context: Context, onSuccess: () -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("titles").document(itemId)
        .delete()
        .addOnSuccessListener {
            onSuccess() // Invoke the success callback, possibly to navigate away or show a toast
        }
        .addOnFailureListener { exception ->
            Toast.makeText(context, "Error deleting item: ${exception.localizedMessage}", Toast.LENGTH_LONG).show()
        }
}