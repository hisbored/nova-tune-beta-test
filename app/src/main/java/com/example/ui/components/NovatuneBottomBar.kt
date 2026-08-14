package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentVioletActivePill
import com.example.ui.theme.AccentVioletPrimary

enum class NovatuneTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    HOME("Home", Icons.Filled.Home, Icons.Outlined.Home, "nav_tab_home"),
    SEARCH("Search", Icons.Filled.Search, Icons.Outlined.Search, "nav_tab_search"),
    SONGS("Songs", Icons.Filled.MusicNote, Icons.Outlined.MusicNote, "nav_tab_songs"),
    PLAYLISTS("Playlists", Icons.Filled.QueueMusic, Icons.Outlined.QueueMusic, "nav_tab_playlists"),
    DOWNLOADS("Downloads", Icons.Filled.Download, Icons.Outlined.Download, "nav_tab_downloads"),
    SETTINGS("Settings", Icons.Filled.Tune, Icons.Outlined.Tune, "nav_tab_settings")
}

@Composable
fun NovatuneBottomBar(
    selectedTab: NovatuneTab,
    onTabSelected: (NovatuneTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = Color.White.copy(alpha = 0.05f)),
        color = Color(0xFF1C1B1F),
        tonalElevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NovatuneTab.entries.forEach { tab ->
                val isSelected = tab == selectedTab

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .testTag(tab.testTag)
                        .clickable { onTabSelected(tab) }
                        .padding(vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 56.dp, height = 30.dp)
                            .clip(RoundedCornerShape(15.dp))
                            .background(
                                if (isSelected) AccentVioletActivePill else Color.Transparent
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                            contentDescription = tab.title,
                            tint = if (isSelected) AccentVioletPrimary else Color(0xFF9E9E9E),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = tab.title,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSelected) AccentVioletPrimary else Color(0xFF9E9E9E),
                        maxLines = 1
                    )
                }
            }
        }
    }
}
