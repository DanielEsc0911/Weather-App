package com.daxter.android.weatherapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.flow.debounce
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.daxter.android.weatherapp.api.NetworkResponse
import com.daxter.android.weatherapp.api.WeatherModel
import kotlinx.coroutines.FlowPreview

@OptIn(FlowPreview::class)
@Composable
fun WeatherPage(viewModel: WeatherViewModel){
    var city by remember { mutableStateOf("") }

    var searchText by remember { mutableStateOf("") }

    LaunchedEffect(key1 = searchText) {
        snapshotFlow { searchText }
            .debounce(500)
            .collect {
                city = it
                if(it.isNotEmpty()){
                    viewModel.getData(it)
                }
            }
    }

    val weatherResult = viewModel.weatherResult.observeAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ){
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = searchText,
                onValueChange = {searchText = it},
                label = { Text(text = "Search location") }
            )

        }

        when(val result = weatherResult.value){
            is NetworkResponse.Error -> {
                Text(text = result.message)
            }
            NetworkResponse.Loading -> {
                CircularProgressIndicator()
            }
            is NetworkResponse.Success -> {
                WeatherDetails(data = result.data)
            }
            null -> {}
        }
    }
}

@Composable
fun WeatherDetails(data: WeatherModel) {
    Column{
        Row (modifier = Modifier.padding(horizontal = 8.dp)){
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 4.dp,
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = data.location.name,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(0.6f)
                        )

                        Text(
                            text = data.location.country,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 8.dp)
                        )

                    }

                    VerticalDivider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp)
                    Row(
                        Modifier.padding(vertical = 48.dp)

                    ) {

                        Text(
                            text = "${data.current.temp_c}°C",
                            style = MaterialTheme.typography.headlineLarge,
                            fontSize = 80.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                    }


                    Text(
                        text = data.current.condition.text,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Row (modifier = Modifier.padding(horizontal = 8.dp)){
            Surface (
                modifier = Modifier
                    .weight(.5f)
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {

                    Row(
                        modifier = Modifier
                            .padding(16.dp)

                    ){
                        Icon(
                            painter = painterResource(R.drawable.ic_water_drop),
                            contentDescription = "Dew point",
                            Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(text = "Dew-point")
                    }

                    Column(modifier = Modifier.padding(top = 28.dp)) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically


                        ) {
                            Text(
                                text = data.current.dewpoint_c,
                                style = MaterialTheme.typography.headlineMedium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
            }
            Surface (
                modifier = Modifier
                    .weight(.5f)
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {

                    Row(
                        modifier = Modifier
                            .padding(16.dp)

                    ){
                        Icon(
                            painter = painterResource(R.drawable.ic_water),
                            contentDescription = "Humidity",
                            Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(text = "Humidity")
                    }

                    Column(modifier = Modifier.padding(top = 28.dp)) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically


                        ) {
                            Text(
                                text = "${data.current.humidity}%",
                                style = MaterialTheme.typography.headlineMedium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
            }
        }
        Row (modifier = Modifier.padding(horizontal = 8.dp)){
            Surface (
                modifier = Modifier
                    .weight(.5f)
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {

                    Row(
                        modifier = Modifier
                            .padding(16.dp)

                    ){
                        Icon(
                            painter = painterResource(R.drawable.ic_compress),
                            contentDescription = "Pressure",
                            Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(text = "Pressure")
                    }

                    Column(modifier = Modifier.padding(top = 28.dp)) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically


                        ) {
                            Text(
                                text = "${data.current.pressure_mb} mb",
                                style = MaterialTheme.typography.headlineMedium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
            }
            Surface (
                modifier = Modifier
                    .weight(.5f)
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {

                    Row(
                        modifier = Modifier
                            .padding(16.dp)

                    ){
                        Icon(
                            painter = painterResource(R.drawable.ic_sun_alt),
                            contentDescription ="UV Index",
                            Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(text = "UV Index")
                    }

                    Column(modifier = Modifier.padding(top = 28.dp)) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically


                        ) {
                            Text(
                                text = data.current.uv, //REFACTOR LATER
                                style = MaterialTheme.typography.headlineMedium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
            }
        }
        Row (modifier = Modifier.padding(horizontal = 8.dp)){
            Surface (
                modifier = Modifier
                    .weight(.5f)
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {

                    Row(
                        modifier = Modifier
                            .padding(16.dp)

                    ){
                        Icon(
                            painter = painterResource(R.drawable.ic_visibility),
                            contentDescription ="Visibility",
                            Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(text = "Visibility")
                    }

                    Column(modifier = Modifier.padding(top = 28.dp)) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically


                        ) {
                            Text(
                                text = "${data.current.vis_km} km",
                                style = MaterialTheme.typography.headlineMedium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
            }
            Surface (
                modifier = Modifier
                    .weight(.5f)
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {

                    Row(
                        modifier = Modifier
                            .padding(16.dp)

                    ){
                        Icon(
                            painter = painterResource(R.drawable.ic_wind_mill),
                            contentDescription = "Wind",
                            Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(text = "Wind")
                    }

                    Column(modifier = Modifier.padding(top = 28.dp)) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically


                        ) {
                            Text(
                                text = "${data.current.wind_kph} km/h",
                                style = MaterialTheme.typography.headlineMedium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
            }
        }
    }
}
