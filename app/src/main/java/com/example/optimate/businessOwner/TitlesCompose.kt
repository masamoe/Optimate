package com.example.optimate.businessOwner

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Role(val title: String, val category: String)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TitlesScreen(roles: List<Role>) {
    Scaffold(
        topBar = { RolesTopAppBar() },
        content = { innerPadding ->
            RolesList(roles,innerPadding)
        }
    )
}

@Composable
fun RolesTopAppBar() {
    val buttonColor = MaterialTheme.colors.run {
        if (isLight) Color(0xFFC4F0E6) else Color(0xFF91C9B7)
    }
    TopAppBar(
        title = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(end = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Titles", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Button(
                    onClick = { /* TODO: Handle add role action */ },
                    colors = ButtonDefaults.buttonColors(backgroundColor = buttonColor),
                    modifier = Modifier
                        .height(40.dp)
                ) {
                    Text("+ Add Title", color = Color.Black, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        },
        backgroundColor = Color.White,
        elevation = 4.dp
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RolesList(roles: List<Role>, innerPadding: PaddingValues) {
    val groupedRoles = roles.groupBy { it.category }
    val combinedPadding = PaddingValues(
        start = innerPadding.calculateStartPadding(LayoutDirection.Ltr) + 16.dp,
        top = innerPadding.calculateTopPadding() + 8.dp,
        end = innerPadding.calculateEndPadding(LayoutDirection.Ltr) + 16.dp,
        bottom = innerPadding.calculateBottomPadding() + 8.dp
    )
    LazyColumn(
        contentPadding = combinedPadding
    ) {
        groupedRoles.forEach { (category, roles) ->
            stickyHeader {
                Header(title = category)
            }
            itemsIndexed(roles) { index, role ->
                RoleRow(role, index)
            }
        }
    }
}
@Composable
fun Header(title: String) {
    Text(
        text = title,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.surface)
            .padding(8.dp),
        style = MaterialTheme.typography.h6,
    )
}

@Composable
fun RoleRow(role: Role, index: Int) {
    val backgroundColor = if (index % 2 == 0) Color(0xFFC0C2EC) else Color(0xFFF2EBF3)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp), // Increased padding from 4.dp to 8.dp
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
            // Role icon and title
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Role Icon",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = role.title, fontSize = 18.sp)
            }

            // Right arrow icon at the end of the card
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = "Go to role details",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TitlesScreenPreview() {
    val sampleRoles = listOf(
        Role("Manager", "Managers:"),
        Role("Head Chef", "Managers:"),
        Role("Server", "Employees:"),
        Role("Line Cook", "Employees:"),
        Role("Dishwasher", "Employees:"),
        Role("Manager", "Managers:"),
        Role("Head Chef", "Managers:"),
        Role("Server", "Employees:"),
        Role("Line Cook", "Employees:"),
        Role("Dishwasher", "Employees:"),
        Role("Manager", "Managers:"),
        Role("Head Chef", "Managers:"),
        Role("Server", "Employees:"),
        Role("Line Cook", "Employees:"),
        Role("Dishwasher", "Employees:")
    )
    MaterialTheme {
        TitlesScreen(sampleRoles)
    }
}