package com.example.recipiesearch.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.recipiesearch.domain.model.Recipie

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
     homeViewModel: HomeViewModel = hiltViewModel()
) {
     val state = homeViewModel.state

     // Listen for navigation events to refresh favorite states
     LaunchedEffect(Unit) {
          homeViewModel.refreshFavoriteStates()
     }

     Column(
          modifier = Modifier
               .fillMaxSize()
               .background(MaterialTheme.colorScheme.background)
     ) {
          HomeTopSection(
               searchQuery = state.searchQuery,
               onSearchQueryChange = { query ->
                    homeViewModel.onEvent(HomeScreenEvent.OnSearchQuery(query))
               }
          )

          HomeContent(
               state = state,
               onToggleFavorite = { recipe ->
                    homeViewModel.onEvent(HomeScreenEvent.ToggleFavorite(recipe))
               }
          )
     }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopSection(
     searchQuery: String,
     onSearchQueryChange: (String) -> Unit
) {
     Column(
          modifier = Modifier
               .fillMaxWidth()
               .background(MaterialTheme.colorScheme.background)
               .padding(16.dp)
     ) {
          HomeHeader()

          Spacer(modifier = Modifier.height(16.dp))

          SearchBar(
               searchQuery = searchQuery,
               onSearchQueryChange = onSearchQueryChange
          )
     }
}

@Composable
private fun HomeHeader() {
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
                         color = MaterialTheme.colorScheme.onBackground
                    )
               }
               Text(
                    text = "Discover tasty and healthy recipe",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
               )
          }
     }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar(
     searchQuery: String,
     onSearchQueryChange: (String) -> Unit
) {
     OutlinedTextField(
          value = searchQuery,
          onValueChange = onSearchQueryChange,
          placeholder = {
               Text(
                    text = "Search any recipe",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
               )
          },
          leadingIcon = {
               Icon(
                    Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
               )
          },
          modifier = Modifier
               .fillMaxWidth()
               .height(56.dp),
          shape = RoundedCornerShape(28.dp),
          colors = OutlinedTextFieldDefaults.colors(
               unfocusedBorderColor = MaterialTheme.colorScheme.outline,
               focusedBorderColor = MaterialTheme.colorScheme.primary,
               unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
               focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
          )
     )
}

@Composable
private fun HomeContent(
     state: HomeScreenState,
     onToggleFavorite: (Recipie) -> Unit
) {
     LazyColumn(
          modifier = Modifier
               .fillMaxSize()
               .background(MaterialTheme.colorScheme.background),
          contentPadding = PaddingValues(16.dp),
          verticalArrangement = Arrangement.spacedBy(16.dp)
     ) {
          // Popular Recipes Section
          item {
               PopularRecipesSection(
                    popularRecipes = state.popularRecipes,
                    isLoading = state.isPopularRecipesLoading,
                    onToggleFavorite = onToggleFavorite
               )
          }

          // All Recipes Section
          item {
               Text(
                    text = "All recipes",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
               )
          }

          // Recipe List
          if (state.isLoading && state.recipes.isEmpty()) {
               items(3) { // Show 3 loading placeholders
                    RecipeListItemLoading()
               }
          } else if (state.error.isNotEmpty()) {
               item {
                    ErrorCard(error = state.error)
               }
          } else {
               items(state.recipes) { recipe ->
                    RecipeListItem(
                         recipe = recipe,
                         onFavoriteClick = { onToggleFavorite(recipe) }
                    )
               }
          }
     }
}

@Composable
private fun PopularRecipesSection(
     popularRecipes: List<Recipie>,
     isLoading: Boolean,
     onToggleFavorite: (Recipie) -> Unit
) {
     Column {
          Text(
               text = "Popular Recipes",
               fontSize = 18.sp,
               fontWeight = FontWeight.SemiBold,
               color = MaterialTheme.colorScheme.onBackground
          )

          Spacer(modifier = Modifier.height(12.dp))

          LazyRow(
               horizontalArrangement = Arrangement.spacedBy(12.dp),
               contentPadding = PaddingValues(horizontal = 4.dp)
          ) {
               if (isLoading) {
                    items(2) { // Show 2 loading placeholders
                         PopularRecipeLoadingCard()
                    }
               } else {
                    items(popularRecipes.take(5)) { recipe ->
                         PopularRecipeCard(
                              recipe = recipe,
                              onFavoriteClick = { onToggleFavorite(recipe) }
                         )
                    }
               }
          }
     }
}

@Composable
private fun ErrorCard(error: String) {
     Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(
               containerColor = MaterialTheme.colorScheme.errorContainer
          )
     ) {
          Text(
               text = error,
               color = MaterialTheme.colorScheme.onErrorContainer,
               modifier = Modifier.padding(16.dp)
          )
     }
}

@Composable
private fun PopularRecipeLoadingCard() {
     Box(
          modifier = Modifier
               .width(200.dp)
               .height(120.dp)
               .clip(RoundedCornerShape(12.dp))
               .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
     ) {
          CircularProgressIndicator(
               modifier = Modifier.align(Alignment.Center),
               strokeWidth = 2.dp,
               color = MaterialTheme.colorScheme.primary
          )
     }
}

@Composable
private fun RecipeListItemLoading() {
     Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(
               containerColor = MaterialTheme.colorScheme.surface
          ),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
     ) {
          Row(
               modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
               verticalAlignment = Alignment.CenterVertically
          ) {
               // Image placeholder
               Box(
                    modifier = Modifier
                         .size(60.dp)
                         .clip(RoundedCornerShape(8.dp))
                         .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
               ) {
                    CircularProgressIndicator(
                         modifier = Modifier
                              .size(20.dp)
                              .align(Alignment.Center),
                         strokeWidth = 2.dp,
                         color = MaterialTheme.colorScheme.primary
                    )
               }

               Spacer(modifier = Modifier.width(12.dp))

               // Text placeholders
               Column(
                    modifier = Modifier.weight(1f)
               ) {
                    Box(
                         modifier = Modifier
                              .width(150.dp)
                              .height(16.dp)
                              .background(
                                   MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                   RoundedCornerShape(4.dp)
                              )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                         modifier = Modifier
                              .width(100.dp)
                              .height(12.dp)
                              .background(
                                   MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                   RoundedCornerShape(4.dp)
                              )
                    )
               }

               // Heart placeholder
               Box(
                    modifier = Modifier
                         .size(24.dp)
                         .background(
                              MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                              CircleShape
                         )
               )
          }
     }
}

@Composable
private fun PopularRecipeCard(
     recipe: Recipie,
     onFavoriteClick: () -> Unit
) {
     Box(
          modifier = Modifier
               .width(200.dp)
               .height(120.dp)
               .clip(RoundedCornerShape(12.dp))
     ) {
          // Background Image or placeholder
          RecipeCardBackground(
               imageUrl = recipe.image,
               title = recipe.title
          )

          // Gradient overlay
          Box(
               modifier = Modifier
                    .fillMaxSize()
                    .background(
                         brush = Brush.verticalGradient(
                              colors = listOf(
                                   androidx.compose.ui.graphics.Color.Transparent,
                                   androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.6f)
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
               FavoriteButton(
                    isFavorite = recipe.isFavourite,
                    onClick = onFavoriteClick,
                    modifier = Modifier.size(28.dp)
               )
          }

          // Content
          Column(
               modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
               verticalArrangement = Arrangement.SpaceBetween
          ) {
               Spacer(modifier = Modifier.weight(1f))

               // Bottom content
               Column {
                    Text(
                         text = recipe.title,
                         color = androidx.compose.ui.graphics.Color.White,
                         fontSize = 16.sp,
                         fontWeight = FontWeight.SemiBold,
                         maxLines = 1,
                         overflow = TextOverflow.Ellipsis
                    )
                    Text(
                         text = "Ready in ${if (recipe.readyInMinutes == 0) 25 else recipe.readyInMinutes} min",
                         color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.9f),
                         fontSize = 12.sp
                    )
               }
          }
     }
}

@Composable
private fun RecipeCardBackground(
     imageUrl: String,
     title: String
) {
     if (imageUrl.isNotEmpty()) {
          AsyncImage(
               model = imageUrl,
               contentDescription = title,
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
                                   MaterialTheme.colorScheme.primary,
                                   MaterialTheme.colorScheme.secondary
                              )
                         )
                    )
          )
     }
}

@Composable
private fun FavoriteButton(
     isFavorite: Boolean,
     onClick: () -> Unit,
     modifier: Modifier = Modifier
) {
     IconButton(
          onClick = onClick,
          modifier = modifier
               .background(
                    color = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.3f),
                    shape = CircleShape
               )
     ) {
          Icon(
               imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
               contentDescription = "Favorite",
               tint = if (isFavorite) androidx.compose.ui.graphics.Color.Red else androidx.compose.ui.graphics.Color.White,
               modifier = Modifier.size(16.dp)
          )
     }
}

@Composable
private fun RecipeListItem(
     recipe: Recipie,
     onFavoriteClick: () -> Unit
) {
     Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(
               containerColor = MaterialTheme.colorScheme.surface
          ),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
     ) {
          Row(
               modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
               verticalAlignment = Alignment.CenterVertically
          ) {
               // Recipe Image
               RecipeListItemImage(
                    imageUrl = recipe.image,
                    title = recipe.title,
                    modifier = Modifier.size(60.dp)
               )

               Spacer(modifier = Modifier.width(12.dp))

               // Recipe Info
               Column(
                    modifier = Modifier.weight(1f)
               ) {
                    Text(
                         text = recipe.title.ifEmpty { "Recipe name goes here" },
                         fontSize = 16.sp,
                         fontWeight = FontWeight.Medium,
                         color = if (recipe.title.isEmpty())
                              MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                         else MaterialTheme.colorScheme.onSurface,
                         maxLines = 2,
                         overflow = TextOverflow.Ellipsis
                    )
                    Text(
                         text = "Ready in ${if (recipe.readyInMinutes == 0) 25 else recipe.readyInMinutes} min",
                         fontSize = 12.sp,
                         color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
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
                         tint = if (recipe.isFavourite) androidx.compose.ui.graphics.Color.Red else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                         modifier = Modifier.size(20.dp)
                    )
               }
          }
     }
}

@Composable
private fun RecipeListItemImage(
     imageUrl: String,
     title: String,
     modifier: Modifier = Modifier
) {
     Box(
          modifier = modifier
               .clip(RoundedCornerShape(8.dp))
               .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
     ) {
          if (imageUrl.isNotEmpty()) {
               AsyncImage(
                    model = imageUrl,
                    contentDescription = title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
               )
          } else {
               // Placeholder for missing image
               Box(
                    modifier = Modifier
                         .fillMaxSize()
                         .background(
                              brush = Brush.linearGradient(
                                   colors = listOf(
                                        MaterialTheme.colorScheme.surfaceVariant,
                                        MaterialTheme.colorScheme.surface
                                   ),
                                   start = Offset(0f, 0f),
                                   end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                              )
                         ),
                    contentAlignment = Alignment.Center
               ) {
                    Text(
                         text = "🍽️",
                         fontSize = 32.sp
                    )
               }
          }
     }
}