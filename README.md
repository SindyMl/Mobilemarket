# MobileMarket 📱

A modern Android marketplace application that enables users to buy and sell items in a secure, user-friendly mobile environment. Built with native Android development and powered by a PHP backend API.

## 🌟 Overview

MobileMarket is a comprehensive e-commerce mobile application designed to facilitate peer-to-peer trading. Users can register, authenticate, browse items, post their own products for sale, and interact with a dynamic marketplace ecosystem. The app features a clean, intuitive interface with real-time data synchronization and robust user management.

## ✨ Key Features

### 🔐 User Authentication
- **Secure Registration & Login**: Complete user authentication system with session management
- **Token-based Security**: JWT-like token system for secure API communications
- **Persistent Sessions**: Auto-login functionality with secure token storage

### 🛍️ Marketplace Functionality
- **Browse Items**: View all available items with search and filtering capabilities
- **Item Details**: Comprehensive item information including ratings, descriptions, and seller details
- **Post Items**: Easy-to-use interface for listing items for sale
- **Top-Rated Items**: Featured section showcasing highest-rated products on the home screen

### 📱 Modern UI/UX
- **Material Design**: Clean, modern interface following Android design guidelines
- **Bottom Navigation**: Intuitive navigation between Home, Browse, and Post sections
- **RecyclerView Integration**: Smooth scrolling and efficient data display
- **Search Functionality**: Real-time search with keyword filtering
- **Responsive Design**: Optimized for various Android screen sizes

### 🔄 Real-time Data
- **Live Updates**: Real-time synchronization with backend database
- **Rating System**: Community-driven item rating and feedback system
- **Dynamic Content**: Automatically updated listings and user data

## 🏗️ Technical Architecture

### Frontend (Android)
- **Language**: Java
- **Minimum SDK**: API 24 (Android 7.0)
- **Target SDK**: API 34 (Android 14)
- **Architecture**: MVVM pattern with Data Binding

### Backend (PHP/MySQL)
- **Server**: PHP-based REST API
- **Database**: MySQL database for data persistence
- **Hosting**: LAMP stack (Linux, Apache, MySQL, PHP)
- **API Endpoints**: RESTful services for all app functionality

### Key Dependencies
- **Volley**: HTTP library for network requests
- **Material Design Components**: Modern UI components
- **AndroidX Navigation**: Fragment navigation architecture
- **Data Binding**: Two-way data binding for UI
- **RecyclerView**: Efficient list display
- **CardView**: Material design card layouts

## 🚀 Getting Started

### Prerequisites

Before setting up the project, ensure you have:

- **Android Studio**: Latest version (recommended: Arctic Fox or newer)
- **Java Development Kit (JDK)**: Version 8 or higher
- **Android SDK**: API level 24-34
- **Git**: For version control
- **Web Server**: Apache/Nginx with PHP support (for backend)
- **MySQL Database**: Version 5.7 or higher

### 📥 Installation

#### 1. Clone the Repository
```bash
git clone https://github.com/SindyMl/Mobilemarket.git
cd Mobilemarket
```

#### 2. Android App Setup

1. **Open in Android Studio**:
   - Launch Android Studio
   - Select "Open an existing project"
   - Navigate to the cloned repository folder
   - Select the project and click "OK"

2. **Gradle Sync**:
   - Android Studio will automatically prompt for Gradle sync
   - Wait for the sync to complete (may take several minutes on first run)

3. **SDK Requirements**:
   - Ensure Android SDK platforms 24-34 are installed
   - Install any missing dependencies suggested by Android Studio

4. **Configuration**:
   - Update API endpoints in the Java files if hosting backend elsewhere
   - Current endpoints point to: `https://lamp.ms.wits.ac.za/home/s2669198/`

#### 3. Backend Setup

1. **Web Server Configuration**:
   ```bash
   # For Apache on Ubuntu/Debian
   sudo apt update
   sudo apt install apache2 php mysql-server php-mysql
   
   # For Windows (XAMPP recommended)
   # Download and install XAMPP from https://www.apachefriends.org/
   ```

2. **Database Setup**:
   ```sql
   CREATE DATABASE d2669198;
   USE d2669198;
   
   -- Create users table
   CREATE TABLE users (
       id INT AUTO_INCREMENT PRIMARY KEY,
       username VARCHAR(255) UNIQUE NOT NULL,
       password VARCHAR(255) NOT NULL,
       email VARCHAR(255) UNIQUE NOT NULL,
       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
   );
   
   -- Create items table
   CREATE TABLE items (
       item_id INT AUTO_INCREMENT PRIMARY KEY,
       seller_id INT NOT NULL,
       name VARCHAR(255) NOT NULL,
       description TEXT,
       price DECIMAL(10,2) NOT NULL,
       rating DECIMAL(3,2) DEFAULT 0.00,
       date_posted TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
       FOREIGN KEY (seller_id) REFERENCES users(id)
   );
   ```

3. **PHP Files Deployment**:
   - Copy all PHP files to your web server directory (e.g., `/var/www/html/` or `htdocs/`)
   - Update database credentials in PHP files:
     ```php
     $username = "your_db_username";
     $password = "your_db_password";
     $database = "your_db_name";
     ```

4. **API Endpoints**:
   - `login.php` - User authentication
   - `register.php` - User registration
   - `get_items.php` - Retrieve marketplace items
   - `post_item.php` - Add new items
   - `rate_item.php` - Rate and review items

### 🔧 Configuration

#### Android App Configuration

1. **Update API URLs**:
   - Open `MainActivity.java`, `LoginActivity.java`, etc.
   - Replace URL constants with your server endpoints:
   ```java
   private static final String LOGIN_URL = "https://your-domain.com/api/login.php";
   ```

2. **Network Security**:
   - For development with HTTP, add to `AndroidManifest.xml`:
   ```xml
   <application android:usesCleartextTraffic="true">
   ```

#### Backend Configuration

1. **Database Connection**:
   - Update `mobileMarket.php` and other PHP files with your database credentials
   - Ensure MySQL service is running

2. **CORS Configuration** (if needed):
   ```php
   header("Access-Control-Allow-Origin: *");
   header("Access-Control-Allow-Methods: GET, POST, OPTIONS");
   header("Access-Control-Allow-Headers: Content-Type, Authorization");
   ```

### 🏃‍♂️ Running the Application

#### Android App
1. Connect an Android device or start an emulator
2. Click "Run" in Android Studio or use:
   ```bash
   ./gradlew installDebug
   ```

#### Backend
1. Start your web server and MySQL service
2. Test API endpoints:
   ```bash
   curl -X GET "http://your-domain.com/get_items.php"
   ```

## 📱 Usage Guide

### Getting Started
1. **Registration**: Create a new account with username, email, and password
2. **Login**: Access your account with registered credentials
3. **Browse**: Explore available items in the marketplace
4. **Search**: Use the search functionality to find specific items
5. **Post Items**: List your own items for sale with descriptions and pricing
6. **Rate Items**: Provide feedback and ratings for purchased items

### Navigation
- **Home Tab**: View top-rated items and welcome dashboard
- **Browse Tab**: Search and filter through all marketplace items
- **Post Tab**: Add new items to the marketplace

## 🛠️ Development

### Project Structure
```
app/
├── src/main/java/com/example/mobilemarket/
│   ├── MainActivity.java          # Home screen with top items
│   ├── LoginActivity.java         # User authentication
│   ├── RegisterActivity.java      # User registration
│   ├── BrowseItemsActivity.java   # Item browsing and search
│   ├── PostItemsActivity.java     # Item posting interface
│   ├── DetailsActivity.java       # Item detail view
│   ├── Item.java                  # Item data model
│   ├── ItemAdapter.java           # RecyclerView adapter
│   └── ui/                        # UI fragments and components
├── src/main/res/
│   ├── layout/                    # XML layout files
│   ├── values/                    # Colors, strings, styles
│   └── drawable/                  # Images and icons
└── build.gradle                   # App dependencies and configuration
```

### Building for Production

1. **Generate Signed APK**:
   - In Android Studio: Build → Generate Signed Bundle/APK
   - Follow the wizard to create/use a keystore

2. **Optimize Build**:
   ```gradle
   android {
       buildTypes {
           release {
               minifyEnabled true
               proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
           }
       }
   }
   ```

## 🔒 Security Considerations

- **Input Validation**: All user inputs are validated on both client and server
- **SQL Injection Prevention**: Prepared statements used in all database queries
- **Authentication**: Token-based authentication for API security
- **HTTPS**: Use HTTPS in production for secure data transmission
- **Password Security**: Implement proper password hashing (bcrypt recommended)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Support

For support and questions:
- Create an issue on GitHub
- Contact the development team
- Check the documentation for common solutions

## 📊 Version History

- **v1.0.0** - Initial release with core marketplace functionality
- Features: User authentication, item browsing, posting, and rating system

---

**Built with ❤️ using Android Studio and modern development practices**