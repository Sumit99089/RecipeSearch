package com.example.recipiesearch.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
     homeViewModel: HomeViewModel = hiltViewModel()
) {
     val state = homeViewModel.state

     Scaffold(
          topBar = {
               Column(
                    modifier = Modifier
                         .fillMaxWidth()
                         .background(Color.White)
                         .padding(16.dp)
               ) {
                    // Header
                    Row(
                         modifier = Modifier.fillMaxWidth(),
                         horizontalArrangement = Arrangement.SpaceBetween,
                         verticalAlignment = Alignment.CenterVertically
                    ) {
                         Column {
                              Row(
                                   verticalAlignment = Alignment.CenterVertically
                              ) {
                                   Text(
                                        text = "👋",
                                        fontSize = 20.sp
                                   )
                                   Spacer(modifier = Modifier.width(8.dp))
                                   Text(
                                        text = "Hey Name",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.Black
                                   )
                              }
                              Text(
                                   text = "Discover tasty and healthy receipt",
                                   fontSize = 14.sp,
                                   color = Color.Gray
                              )
                         }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Search Bar
                    OutlinedTextField(
                         value = state.searchQuery,
                         onValueChange = {
                              homeViewModel.onEvent(HomeScreenEvent.OnSearchQuery(it))
                         },
                         placeholder = {
                              Text(
                                   text = "Search any recipe",
                                   color = Color.Gray
                              )
                         },
                         leadingIcon = {
                              Icon(
                                   Icons.Default.Search,
                                   contentDescription = "Search",
                                   tint = Color.Gray
                              )
                         },
                         modifier = Modifier
                              .fillMaxWidth()
                              .height(56.dp),
                         shape = RoundedCornerShape(28.dp),
                         colors = OutlinedTextFieldDefaults.colors(
                              unfocusedBorderColor = Color.LightGray,
                              focusedBorderColor = Color.Gray,
                              unfocusedContainerColor = Color(0xFFF5F5F5),
                              focusedContainerColor = Color(0xFFF5F5F5)
                         )
                    )
               }
          },
          bottomBar = {
               BottomNavigationBar()
          }
     ) { paddingValues ->
          LazyColumn(
               modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.White),
               contentPadding = PaddingValues(16.dp),
               verticalArrangement = Arrangement.spacedBy(16.dp)
          ) {
               // Popular Recipes Section
               item {
                    Column {
                         Text(
                              text = "Popular Recipes",
                              fontSize = 18.sp,
                              fontWeight = FontWeight.SemiBold,
                              color = Color.Black
                         )

                         Spacer(modifier = Modifier.height(12.dp))

                         LazyRow(
                              horizontalArrangement = Arrangement.spacedBy(12.dp),
                              contentPadding = PaddingValues(horizontal = 4.dp)
                         ) {
                              if (state.isLoading) {
                                   item {
                                        Box(
                                             modifier = Modifier.size(200.dp, 120.dp),
                                             contentAlignment = Alignment.Center
                                        ) {
                                             CircularProgressIndicator()
                                        }
                                   }
                              } else {
                                   items(state.popularRecipies.take(5)) { recipe ->
                                        PopularRecipeCard(
                                             recipe = recipe,
                                             onFavoriteClick = {
                                                  homeViewModel.onEvent(HomeScreenEvent.ToggleFavorite(recipe))
                                             }
                                        )
                                   }
                              }
                         }
                    }
               }

               // All Recipes Section
               item {
                    Text(
                         text = "All recipes",
                         fontSize = 18.sp,
                         fontWeight = FontWeight.SemiBold,
                         color = Color.Black
                    )
               }

               // Recipe List
               if (state.isLoading) {
                    item {
                         Box(
                              modifier = Modifier.fillMaxWidth(),
                              contentAlignment = Alignment.Center
                         ) {
                              CircularProgressIndicator()
                         }
                    }
               } else if (state.error.isNotEmpty()) {
                    item {
                         Card(
                              modifier = Modifier.fillMaxWidth(),
                              colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
                         ) {
                              Text(
                                   text = state.error,
                                   color = Color(0xFFE65100),
                                   modifier = Modifier.padding(16.dp)
                              )
                         }
                    }
               } else {
                    items(state.recipies) { recipe ->
                         RecipeListItem(
                              recipe = recipe,
                              onFavoriteClick = {
                                   homeViewModel.onEvent(HomeScreenEvent.ToggleFavorite(recipe))
                              }
                         )
                    }
               }
          }
     }
}

@Composable
fun PopularRecipeCard(
     recipe: com.example.recipiesearch.domain.model.Recipie,
     onFavoriteClick: () -> Unit
) {
     Box(
          modifier = Modifier
               .width(200.dp)
               .height(120.dp)
               .clip(RoundedCornerShape(12.dp))
     ) {
          // Background Image or placeholder
          if (recipe.image.isNotEmpty()) {
               AsyncImage(
                    model = recipe.image,
                    contentDescription = recipe.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
               )
          } else {
               Box(
                    modifier = Modifier
                         .fillMaxSize()
                         .background(
                              brush = Brush.horizontalGradient(
                                   colors = listOf(
                                        Color(0xFFFF9800),
                                        Color(0xFFFF5722)
                                   )
                              )
                         )
               )
          }

          // Gradient overlay
          Box(
               modifier = Modifier
                    .fillMaxSize()
                    .background(
                         brush = Brush.verticalGradient(
                              colors = listOf(
                                   Color.Transparent,
                                   Color.Black.copy(alpha = 0.6f)
                              )
                         )
                    )
          )

          // Favorite button
          Box(
               modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
               contentAlignment = Alignment.TopEnd
          ) {
               IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier
                         .size(28.dp)
                         .background(
                              color = Color.Black.copy(alpha = 0.3f),
                              shape = CircleShape
                         )
               ) {
                    Icon(
                         imageVector = if (recipe.isFavourite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                         contentDescription = "Favorite",
                         tint = if (recipe.isFavourite) Color.Red else Color.White,
                         modifier = Modifier.size(16.dp)
                    )
               }
          }

          // Content
          Column(
               modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
               verticalArrangement = Arrangement.SpaceBetween
          ) {
               // Empty space for top
               Spacer(modifier = Modifier.weight(1f))

               // Bottom content
               Column {
                    Text(
                         text = recipe.title,
                         color = Color.White,
                         fontSize = 16.sp,
                         fontWeight = FontWeight.SemiBold,
                         maxLines = 1,
                         overflow = TextOverflow.Ellipsis
                    )
                    Text(
                         text = "Ready in ${recipe.readyInMinutes} min",
                         color = Color.White.copy(alpha = 0.9f),
                         fontSize = 12.sp
                    )
               }
          }
     }
}

@Composable
fun RecipeListItem(
     recipe: com.example.recipiesearch.domain.model.Recipie,
     onFavoriteClick: () -> Unit
) {
     Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
     ) {
          Row(
               modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
               verticalAlignment = Alignment.CenterVertically
          ) {
               // Recipe Image
               Box(
                    modifier = Modifier
                         .size(60.dp)
                         .clip(RoundedCornerShape(8.dp))
                         .background(Color.LightGray)
               ) {
                    if (recipe.image.isNotEmpty()) {
                         AsyncImage(
                              model = recipe.image,
                              contentDescription = recipe.title,
                              modifier = Modifier.fillMaxSize(),
                              contentScale = ContentScale.Crop
                         )
                    }
               }

               Spacer(modifier = Modifier.width(12.dp))

               // Recipe Info
               Column(
                    modifier = Modifier.weight(1f)
               ) {
                    Text(
                         text = recipe.title,
                         fontSize = 16.sp,
                         fontWeight = FontWeight.Medium,
                         color = Color.Black,
                         maxLines = 2,
                         overflow = TextOverflow.Ellipsis
                    )
                    Text(
                         text = "Ready in ${recipe.readyInMinutes} min",
                         fontSize = 12.sp,
                         color = Color.Gray
                    )
               }

               // Favorite Button
               IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier.size(24.dp)
               ) {
                    Icon(
                         imageVector = if (recipe.isFavourite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                         contentDescription = "Favorite",
                         tint = if (recipe.isFavourite) Color.Red else Color.Gray,
                         modifier = Modifier.size(20.dp)
                    )
               }
          }
     }
}

@Composable
fun BottomNavigationBar() {
     NavigationBar(
          containerColor = Color.White,
          contentColor = Color.Gray
     ) {
          NavigationBarItem(
               icon = {
                    Icon(
                         Icons.Default.Home,
                         contentDescription = "Home"
                    )
               },
               label = { Text("Home") },
               selected = true,
               onClick = { }
          )
          NavigationBarItem(
               icon = {
                    Icon(
                         Icons.Default.Favorite,
                         contentDescription = "Favourite"
                    )
               },
               label = { Text("Favourite") },
               selected = false,
               onClick = { }
          )
     }
}