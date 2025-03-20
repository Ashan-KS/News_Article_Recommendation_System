# News Article Recommendation System

## Overview
The **News Article Recommendation System** is an AI-powered platform that suggests relevant news articles based on user preferences and reading history. The system categorizes articles into 10 categories using keyword extraction from both the content and the headline. 

### How It Works
- 📌 **Article Categorization**: Articles are classified into 10 categories using keyword-based classification.
- 📖 **User Interaction Tracking**: When a user reads an article, it is stored as "viewed."
- 👍👎 **Preference Learning**: If a user likes or dislikes an article, their preferences are updated accordingly.
- 🤖 **AI-Powered Recommendations**: Using **Llama 3.2**, the system generates 5 recommended articles tailored to the user's interests.

---

## Installation & Setup

### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/News_Article_Recommendation.git
cd News_Article_Recommendation
```

### 2. Install and Configure Ollama
#### a) Download and Install Ollama
- Visit the official Ollama website: [Ollama Llama 3.2](https://ollama.com/library/llama3.2).
- Download and install the **Llama 3.2 model** on your PC.

#### b) Install the Model Locally
Open the **Command Prompt (CMD)** and execute the following command:
```bash
ollama run llama3.2
```
Ensure the model is successfully installed and ready for use.

#### c) Keep Ollama Running
Once the model is installed, make sure **Ollama is running in the background** for the program to access it.

---

## Running the Program
To start the system:

1. **Run the Server.java**
2. **Run the Client_Interface.java**

---

## Configuration
The recommendation function relies on **Llama 3.2** for generating personalized article suggestions. When a user reads or rates an article, the system updates their preferences and reading history. These inputs are fed into **Llama 3.2** in a structured prompt to generate five recommended articles.

---

