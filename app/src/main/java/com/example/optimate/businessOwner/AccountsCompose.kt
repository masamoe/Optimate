package com.example.optimate.businessOwner

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import com.example.optimate.employeeFlow.NoDataFound

data class Account(val name: String, val title: String, val uid: String)
@Composable
fun AccountsScreen(accounts: List<Account>) {
    val context = LocalContext.current
    Scaffold(
        topBar = { XmlTopBar(titleText = "Accounts") },
        content = { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),

                    horizontalArrangement = Arrangement.End
                ) {
                    AddAccountButton()
                }
                if (accounts.isEmpty()) {
                    NoDataFound("No accounts found")
                } else {
                    AccountsList(accounts) { account ->
                        val intent = Intent(context, EditAccountActivity::class.java).apply {
                            putExtra("account_name", account.name)
                            putExtra("account_title", account.title)
                            putExtra("account_uid", account.uid)
                        }
                        context.startActivity(intent)
                    }
                }
            }
        }
    )
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun AddAccountButton(modifier: Modifier = Modifier) {
    val buttonColor = MaterialTheme.colors.run { if (isLight) Color(0xFF75f8e2) else Color(0xFF91C9B7) }
    val addAccountBtn = LocalContext.current

    Button(
        onClick = {
            val intent = Intent(addAccountBtn, AddAccountActivity::class.java)
            addAccountBtn.startActivity(intent)
        },
        colors = ButtonDefaults.buttonColors(backgroundColor = buttonColor),
        modifier = modifier
            .padding(16.dp)
            .height(40.dp),
        elevation = ButtonDefaults.elevation(defaultElevation = 8.dp, pressedElevation = 16.dp)
    ) {
        Text(text = "Add Account", color = Color.Black, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
    }

}

@Composable
fun AccountsList(accounts: List<Account>, onClickAccount: (Account) -> Unit) {
    LazyColumn {
        itemsIndexed(accounts) { index, account ->
            AccountRow(account, index) {
                onClickAccount(account)
            }
        }
    }
}

@Composable
fun AccountRow(account: Account, index: Int, onClick:() ->Unit){
    val backgroundColor = if (index % 2 == 0) Color(0xFFC0C2EC) else Color(0xFFF2EBF3)
    val titleColor = Color(0xFF007FFF)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(8.dp),
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
                Column(verticalArrangement = Arrangement.Center) {
                    Text(text = account.name, fontSize = 16.sp)
                    Text(text = account.title, fontSize = 13.sp, color = titleColor)

                }
            }

            // Right arrow icon at the end of the card
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = "Go to account details",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Preview
@Composable
fun PreviewAccountsScreen() {

    val emptyList = emptyList<Account>()
    val accounts = listOf(
        Account("John Doe", "Accountant", "uid1"),
        Account("Jane Doe", "Accountant", "uid2"),
        Account("John Smith", "Accountant", "uid3"),
        Account("Jane Smith", "Accountant", "uid4"),
        Account("John Doe", "Accountant", "uid5"),
        Account("Jane Doe", "Accountant", "uid6"),
        Account("John Smith", "Accountant", "uid7"),
        Account("Jane Smith", "Accountant", "uid8"),
        Account("John Doe", "Accountant", "uid9"),
        Account("Jane Doe", "Accountant", "uid10"),
        Account("John Smith", "Accountant", "uid11"),
        Account("Jane Smith", "Accountant", "uid12"),
        Account("John Doe", "Accountant", "uid13"),
        Account("Jane Doe", "Accountant", "uid14"),
        Account("John Smith", "Accountant", "uid15"),
        Account("Jane Smith", "Accountant", "uid16"),
        Account("John Doe", "Accountant", "uid17"),
        Account("Jane Doe", "Accountant", "uid18"),
        Account("John Smith", "Accountant", "uid19"),
        Account("Jane Smith", "Accountant", "uid20"),
    )
    AccountsScreen(emptyList)
}
