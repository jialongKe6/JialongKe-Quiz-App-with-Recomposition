package com.example.quizappwithrecomposition

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import androidx.compose.material3.TextField
import androidx.compose.ui.window.Dialog

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuizScreen()
        }
    }
}

data class Question(val text: String, val answer: String)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun QuizScreen() {
    val questions = listOf(
        Question("What is the largest planet in our Solar System?", "Jupiter"),
        Question("How many continents are there on Earth?", "7"),
        Question("What is the chemical symbol for water?", "H2O"),
        Question("Who wrote the play 'Romeo and Juliet'?", "Shakespeare"),
        Question("Which country is known as the Land of the Rising Sun?", "Japan"),
        Question("What is the square root of 64?", "8"),
        Question("What year did the Titanic sink?", "1912"),
        Question("Who painted the Mona Lisa?", "Leonardo da Vinci"),
        Question("What is the capital of Australia?", "Canberra"),
        Question("How many bones are there in the adult human body?", "206")
    )

    var currentQuestionIndex by remember { mutableStateOf(0) }
    var userInput by remember { mutableStateOf("") }
    var isQuizComplete by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var attemptCount by remember { mutableStateOf(0) } // Track incorrect attempts
    var showSkipDialog by remember { mutableStateOf(false) } // Control dialog visibility

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            if (!isQuizComplete) {
                // Display the current question
                OutlinedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                ) {
                    Text(
                        text = questions[currentQuestionIndex].text,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = userInput,
                    onValueChange = { userInput = it },
                    label = { Text("Your Answer") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Submit button
                Button(
                    onClick = {
                        val currentQuestion = questions[currentQuestionIndex]
                        if (userInput.equals(currentQuestion.answer, ignoreCase = true)) {
                            // Reset attempt count on correct answer
                            attemptCount = 0
                            if (currentQuestionIndex < questions.size - 1) {
                                currentQuestionIndex++
                                userInput = "" // Clear input
                                scope.launch {
                                    snackbarHostState.showSnackbar("Correct! Moving to next question.")
                                }
                            } else {
                                isQuizComplete = true
                                scope.launch {
                                    snackbarHostState.showSnackbar("Quiz Complete!")
                                }
                            }
                        } else {
                            attemptCount++
                            if (attemptCount >= 3) {
                                showSkipDialog = true // Show skip dialog on 3 incorrect attempts
                            } else {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Incorrect, try again.")
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Submit Answer")
                }

                if (showSkipDialog) {
                    // Show dialog when 3 incorrect attempts are made
                    Dialog(onDismissRequest = { showSkipDialog = false }) {
                        Card(
                            modifier = Modifier.padding(16.dp),
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    text = "You have made 3 incorrect attempts. Do you want to skip this question?",
                                    fontSize = 18.sp,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    Button(onClick = {
                                        // Skip to next question
                                        attemptCount = 0
                                        if (currentQuestionIndex < questions.size - 1) {
                                            currentQuestionIndex++
                                            userInput = ""
                                        }
                                        showSkipDialog = false
                                    }) {
                                        Text("Skip Question")
                                    }
                                    Button(onClick = {
                                        // Dismiss dialog and let user try again
                                        attemptCount = 0
                                        showSkipDialog = false
                                    }) {
                                        Text("Keep")
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                Text(
                    text = "Quiz Complete!",
                    fontSize = 24.sp,
                    modifier = Modifier.padding(16.dp)
                )
                Button(
                    onClick = {
                        currentQuestionIndex = 0
                        userInput = ""
                        isQuizComplete = false
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Restart Quiz")
                }
            }
        }
    }
}
