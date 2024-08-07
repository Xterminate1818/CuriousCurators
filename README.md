# CardCollectionTracker
Project for team Curious Curators

## Contributors
Frank Davis IV,
Genaro Gutierrez,
Jalen Barnes,
Logan Gatlin

## Description
This app is designed to simplify the management of your Pokémon card collections. Whether you're hunting for new cards or keeping track of your existing collection, our app provides a seamless experience for organizing and managing your cards with ease. Discover new additions, track your inventory, and enhance your collection effortlessly with our user-friendly interface.

- Our motivation for creating this project was the desire to have a platform where users can easily search for Pokémon cards and track their own collections. We aimed to provide an intuitive and comprehensive tool for Pokémon card enthusiasts.
- The application was built to offer a user-friendly platform for Pokémon card collectors to catalog and monitor their collections, ensuring easy access to card details and images.

## Key Features
- Search Functionality: Users can easily search for specific Pokémon cards by name, artist, and set.
- Collection Management: Add or remove cards from your personal collection which is dynamically updated in the collection view.
- Recently Viewed Cards: A dedicated section for quickly accessing recently viewed cards.
- Dynamic Image Display: Dynamically display images for each card from the data set.
- Random Card Button: A wildcard option to randomly display a card out of the data set of 17,915 cards.
- I/O: Retrieve data from cards.json asset to display cards and save data in cardsOwned to track collected cards after restarting the app.
- User-Friendly Interface: Simple and intuitive UI following the MVC pattern.

## Installation
- Clone the repository
- Open Project in Android Studio
- Ensure the environment is set to use API 35 or higher using the original PIXEL device.
![2024-08-06](https://github.com/user-attachments/assets/236556c7-89a0-4e0c-aee3-aaabb8b5d6a6)


## Usage
To use the Pokémon Cards Collection Tracker application:
- Launch the app on your device.
- The main screen displays recently viewed Pokémon cards, a counter for both cards collected and cards within the data set, and a Random Card button to get you started on your adventure.
- The collection screen displays your personal collection where you can manage your owned cards.
- The search screen allows you to search and sort by different methods.
- Tap on any card to view detailed information about it, Add it to Collection/Remove from Collection or just hit return.

## Data
The data we utilized for the project was collected from the https://tcgdex.dev/ API. It is a collection of every Pokémon card printed to date, over 17,000 in total. Each line of the ‘cards.json’ file represents a distinct card represented as a JSON blob. Each card has a set of shared fields, and a set of category specific fields for the Pokémon, Trainer, and Energy card categories. Because of inconsistency in card printings over time, many fields may be empty for certain cards. These fields have been considered on a case-by-case basis, and assigned unique “null” filler values. For example, when the illustrator field is missing it is instead given the value “Unknown,” while a missing description is assigned an empty string.
