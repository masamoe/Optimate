package com.example.optimate.businessOwner

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Button
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.ButtonDefaults
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Card
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Icon
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.MaterialTheme
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Scaffold
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.optimate.R
import com.example.optimate.employeeFlow.NoDataFound
import com.example.optimate.loginAndRegister.DynamicLandingActivity

data class Title(val title: String, val category: String)
@Composable
fun TitlesScreen(titles: List<Title>) {
    val context = LocalContext.current
    Scaffold(
        topBar = { XmlTopBar() },
        content = { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                if (titles.isEmpty()) {
                    NoDataFound(text = "No titles found")
                } else {
                    TitlesList(titles) { title ->
                        // This lambda is called when a title is clicked
                        val intent = Intent(context, EditTitleDetailsActivity::class.java).apply {
                            putExtra("title_name", title.title) // Pass the title name to the EditTitleDetailsActivity
                            Log.d("TitleTransfer", "Sending title_name: ${title.title}")
                        }
                        context.startActivity(intent)
                    }

                }
                AddTitleButton(modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 5.dp)) // Button appears over the list
            }
        }
    )
}
@SuppressLint("InflateParams")
@Composable
fun XmlTopBar(titleText: String = "Titles") {
    val context = LocalContext.current // Get the current Compose context
    AndroidView(
        factory = {
            // Inflate the XML layout
            LayoutInflater.from(context).inflate(R.layout.topbar, null, false)
        },
        update = { view ->
            // Cast the view to ConstraintLayout
            val layout = view as ConstraintLayout

            // Set the title text
            val titleTextView: TextView = layout.findViewById(R.id.topBarTitle)
            titleTextView.text = titleText

            // Set the click listener for the home button
            val imageView: ImageView = layout.findViewById(R.id.homeBtn)
            imageView.setImageResource(R.drawable.home_btn) // Ensure this is the correct drawable resource
            imageView.contentDescription = context.getString(R.string.home) // Set content description for accessibility
            imageView.setOnClickListener {
                // Navigate to the BusinessLanding activity
                val intent = Intent(context, DynamicLandingActivity::class.java)
                context.startActivity(intent)
            }
        }
    )
}
@Composable
fun AddTitleButton(modifier: Modifier = Modifier) {
        val addTitleBtn = LocalContext.current
        val buttonColor = MaterialTheme.colors.run {
            if (isLight) Color(0xFF75f8e2) else Color(0xFF91C9B7)}
        Button(
            onClick = {
                        val intent = Intent(addTitleBtn, AddTitle::class.java)
                        addTitleBtn.startActivity(intent)
                      },
            colors = ButtonDefaults.buttonColors(backgroundColor = buttonColor),
            modifier = modifier
                .padding(6.dp)
                .height(40.dp),
            //add shadow to the button
            elevation = ButtonDefaults.elevation(defaultElevation = 8.dp, pressedElevation = 16.dp)
        ) {
            Text("+ Add Title", color = Color.Black, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        }
}
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TitlesList(titles: List<Title>, onClickTitle: (Title) -> Unit) { // Add onClickTitle lambda parameter
    val groupedTitles = titles.groupBy { it.category }
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        groupedTitles.forEach { (category, titles) ->
            stickyHeader {
                Header(title = category)
            }
            itemsIndexed(titles) { index, title ->
                TitleRow(title, index) {
                    onClickTitle(title) // Call onClickTitle with the title
                }
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
fun TitleRow(title: Title, index: Int, onClick: () -> Unit) {
    val backgroundColor = if (index % 2 == 0) Color(0xFFC0C2EC) else Color(0xFFF2EBF3)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
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
                Text(text = title.title, fontSize = 18.sp)
            }

            // Right arrow icon at the end of the card
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = "Go to title details",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
@Preview(showBackground = true)
@Composable
fun TitlesScreenPreview() {
    val emptyList= emptyList<Title>()
    val sampleTitles = listOf(
        Title("Manager", "Management"),
        Title("Supervisor", "Management"),
        Title("Cashier", "Sales"),
        Title("Sales Associate", "Sales"),
        Title("Stock Clerk", "Sales"),
        Title("Janitor", "Maintenance"),
        Title("Security Guard", "Maintenance"),
    )
    MaterialTheme {
        TitlesScreen(sampleTitles)
    }
}