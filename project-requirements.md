# Project Requirements

## Core Functional Requirements

1. **Minimum Viable Product (MVP)**:
    - [x] **Required Features**: registration or login, match with drivers or passengers, accept or deny found match.
    - [x]  **Secondary Features**: ride history, account update information, view weather outside.

2. **Firebase Integration**:
    - Use **Firebase** for at least one key functionality:
        - [x] **User Authentication**
          - user registration and login, password secured, first time users must receive a confirmation link to verify themseves - aka showcase that they have access to the `.edu` email.
        - [x] **Firestore for data storage**
          - user account information
          - ride history
          - passenger history
      
3. **API Integration**:
    - [x] we integrated with OpenWeatherMap.org API to inform our riders and passengers about the weather outside. However, due to limited access of the free usage of API not all cities will have such option, unfortunately. 

4. **Database Usage**:
    - [x] Use of **Firebase Firestore** to store user data persistently, supporting at least basic **CRU~~D~~ operations** (Create, Read, Update, ~~Delete~~).

## UI and UX Requirements

5. **Navigation**:
    - [x] **Tab Navigation**: Include at least two primary navigation tabs for easy access to main features.
    - [x] **Flow Navigation**: Implement structured navigation flows (e.g., detail screens, screen transitions).

6. **Screen Archetypes**:
    - [x] We have three main screens Main screen - where user can request rides, Ride History - where user can view his/her ride history or passenger pick-up history, and Profile screen where user is able to update their profile information.

## Additional Project Requirements

7. **Mobile-Specific Features**:
    - Incorporate at least one mobile-specific capability (e.g., camera, GPS, sensors) that adds unique value to the mobile experience.

8. **UI Consistency**:
    - [x] Our UI is consistent throughout all pages and events, it is very minimalistic and straightforward.

9. **Link to these requirements**:
    - [x] Has been linked in the begining of the README.md file under "Important Links" section.