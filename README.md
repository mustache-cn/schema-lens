# Schema Lens

<div align="center">

![Schema Lens](https://img.shields.io/badge/SchemaLens-DataGrip%20Plugin-blue?style=for-the-badge)
![License](https://img.shields.io/badge/license-Apache%202.0-green?style=for-the-badge)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-purple?style=for-the-badge&logo=kotlin)
![IntelliJ Platform](https://img.shields.io/badge/IntelliJ%20Platform-2024.3+-orange?style=for-the-badge)

**A powerful DataGrip plugin for comprehensive database schema inspection and visualization**

[Features](#-features) • [Installation](#-installation) • [Usage](#-usage) • [Development](#-development) • [Contributing](#-contributing)

</div>

---

## 📖 Overview

Schema Lens is an open-source DataGrip plugin that provides a comprehensive, tabbed interface for viewing database table structures. Inspired by professional database management tools, SchemaLens offers an intuitive way to inspect tables, indexes, foreign keys, triggers, constraints, and more—all in one convenient view.

### Why Schema Lens?

- 🔍 **Comprehensive View**: See all table metadata in organized tabs
- 🎨 **User-Friendly Interface**: Clean, intuitive design with multi-line text support
- ⚡ **Performance Optimized**: Efficient data extraction and rendering
- 🌙 **Dark Mode Support**: Automatic theme adaptation
- 🔧 **Extensible Architecture**: Well-structured codebase for easy customization

---

## ✨ Features

### 📊 Structure Tab
View detailed column information including:
- Column name and data type
- Nullability (NULL/NOT NULL)
- Default values
- Primary key indicators
- Auto-increment flags
- Column comments

### 🔑 Index Tab
Comprehensive index information:
- Index name and type (PRIMARY, UNIQUE, INDEX)
- Associated columns
- Uniqueness indicators
- Index comments

### 🔗 Foreign Key Tab
Complete foreign key relationships:
- Foreign key name and columns
- Referenced table and columns
- Referenced database/schema
- Update and delete rules (CASCADE, RESTRICT, etc.)
- Foreign key comments

### ⚡ Trigger Tab
View all table triggers:
- Trigger name and events (INSERT, UPDATE, DELETE)
- Trigger timing (BEFORE, AFTER)
- Complete trigger statements
- Trigger comments

### ✅ Check Tab
Inspect check constraints:
- Constraint name
- Check condition/expression
- Constraint comments

### 💬 Comment Tab
View table-level comments with:
- Full comment text display
- Multi-line support
- Dark mode compatibility

### 📝 SQL Preview Tab
View complete `CREATE TABLE` statements with:
- Full SQL statement display
- Dark mode support
- Copy to clipboard functionality
- Select all support
- Horizontal and vertical scrolling

### 🎯 Additional Features
- **Long Text Support**: Multi-line tooltips and double-click popup for viewing full content
- **Smart Rendering**: Automatic text wrapping and HTML escaping
- **Resource Management**: Proper cleanup of UI components and listeners
- **Error Handling**: Graceful error handling with user-friendly messages
- **Background Loading**: Non-blocking data extraction for better UX

---

## 🚀 Installation

### From JetBrains Marketplace (Recommended)

1. Open DataGrip
2. Go to `File` → `Settings` → `Plugins` (or `Preferences` → `Plugins` on macOS)
3. Click `Marketplace`
4. Search for "Schema Lens"
5. Click `Install`
6. Restart DataGrip

### From Source

1. Clone the repository:
   ```bash
   git clone https://github.com/mustache-cn/schema-lens.git
   cd schemalens
   ```

2. Build the plugin:
   ```bash
   ./gradlew buildPlugin
   ```

3. Install manually:
   - Go to `File` → `Settings` → `Plugins`
   - Click the gear icon → `Install Plugin from Disk...`
   - Select the `.zip` file from `build/distributions/`
   - Restart DataGrip

---

## 📖 Usage

### Opening Table Structure View

1. **Via Context Menu**:
   - Navigate to your database in the Database tool window
   - Right-click on a table
   - Select `Table Structure`

2. **Via Tools Menu**:
   - Select a table in the Database tool window
   - Go to `Tools` → `Table Structure`

### Navigating Tabs

The table structure view opens in a new editor tab with the following tabs:

- **Structure**: Column details
- **Index**: Index information
- **Foreign Key**: Foreign key relationships
- **Trigger**: Trigger definitions
- **Check**: Check constraints
- **Comment**: Table comments
- **SQL Preview**: Complete CREATE TABLE statement

### Viewing Long Text

- **Tooltip**: Hover over cells with long text to see a multi-line tooltip
- **Full View**: Double-click any cell to open a popup with the complete content
- **Copy**: Right-click in SQL Preview tab to copy SQL to clipboard

---

## 🏗️ Architecture

SchemaLens follows a clean, layered architecture:

```
src/main/kotlin/cn/com/mustache/plugin/schemalens/
├── TableStructureAction.kt          # Main entry point
├── framework/                        # Core framework layer
│   ├── TableStructureLoaderTask.kt  # Background data loading
│   ├── TableStructureEditor.kt       # Editor utilities
│   ├── TableStructureEditorProvider.kt # Editor registration
│   ├── TableStructureVirtualFile.kt  # Virtual file system
│   ├── TableStructureConstants.kt    # Shared constants
│   └── TableStructureLogger.kt       # Centralized logging
├── model/                            # Data models
│   ├── ColumnStructure.kt
│   ├── IndexStructure.kt
│   ├── ForeignKeyStructure.kt
│   ├── TriggerStructure.kt
│   ├── CheckStructure.kt
│   └── TableStructureData.kt
├── extractor/                        # Data extraction layer
│   ├── TableStructureExtractor.kt
│   ├── IndexExtractor.kt
│   ├── ForeignKeyExtractor.kt
│   ├── TriggerExtractor.kt
│   ├── CheckExtractor.kt
│   └── TableCommentExtractor.kt
└── ui/                               # UI components
    ├── TableStructureFileEditor.kt   # Main editor
    ├── ColumnStructureTableModel.kt
    ├── IndexTableModel.kt
    ├── ForeignKeyTableModel.kt
    ├── TriggerTableModel.kt
    ├── CheckTableModel.kt
    ├── MultilineTableCellRenderer.kt
    └── TableCellPopupListener.kt
```

### Design Principles

- **Separation of Concerns**: Clear separation between data extraction, models, and UI
- **Single Responsibility**: Each class has a focused purpose
- **Error Resilience**: Graceful error handling at all layers
- **Performance**: Optimized string operations and resource management
- **Extensibility**: Easy to add new tabs or features

---

## 🛠️ Development

### Prerequisites

- JDK 21 or higher
- IntelliJ IDEA or DataGrip 2024.3+
- Gradle 8.0+

### Building

```bash
# Build the plugin
./gradlew buildPlugin

# Run tests
./gradlew test

# Run plugin in sandbox IDE
./gradlew runIde

# Clean build
./gradlew clean buildPlugin
```

### Project Structure

```
schemalens/
├── src/
│   ├── main/
│   │   ├── kotlin/           # Kotlin source code
│   │   └── resources/
│   │       └── META-INF/
│   │           └── plugin.xml # Plugin configuration
│   └── test/
│       └── kotlin/           # Unit tests
├── build.gradle.kts          # Build configuration
├── settings.gradle.kts       # Project settings
└── README.md                 # This file
```

### Code Style

- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable and function names
- Add KDoc comments for public APIs
- Keep methods focused and single-purpose
- Extract constants to `TableStructureConstants`

### Running Tests

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests "IndexExtractorTest"
```

---

## 🤝 Contributing

We welcome contributions! Here's how you can help:

### Reporting Issues

- Check existing issues first
- Provide detailed reproduction steps
- Include DataGrip version and database type
- Attach relevant logs if available

### Submitting Pull Requests

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass (`./gradlew test`)
6. Commit your changes (`git commit -m 'Add amazing feature'`)
7. Push to the branch (`git push origin feature/amazing-feature`)
8. Open a Pull Request

### Development Guidelines

- Write clear, self-documenting code
- Add unit tests for new features
- Update documentation as needed
- Follow existing code style
- Keep commits atomic and well-described

---

## 📋 Requirements

- **DataGrip**: 2024.3 or later
- **IntelliJ Platform**: 2024.3+
- **Java**: 21+
- **Kotlin**: 1.9+

### Supported Databases

Schema Lens works with all databases supported by DataGrip, including:
- MySQL / MariaDB
- PostgreSQL
- Oracle
- SQL Server
- SQLite
- And more...

---

## 🐛 Troubleshooting

### Plugin Not Appearing in Menu

- Ensure you've selected a table (not a column or other object)
- Check that the database connection is active
- Verify the plugin is enabled in `Settings` → `Plugins`
- Try restarting DataGrip

### Missing Data in Tabs

- Some databases may not support all features (e.g., triggers, check constraints)
- Check DataGrip logs: `Help` → `Show Log in Finder/Explorer`
- Ensure you have proper database permissions
- Verify database driver is up to date

### Performance Issues

- Large tables with many indexes/foreign keys may take time to load
- The plugin loads data in background threads to avoid blocking UI
- Check database connection performance

### Getting Help

- Check [Issues](https://github.com/mustache-cn/schema-lens/issues) for known problems
- Create a new issue with detailed information
- Include DataGrip version and database type

---

## 📄 License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

---

## 🙏 Acknowledgments

- Inspired by professional database management tools
- Built with [IntelliJ Platform SDK](https://plugins.jetbrains.com/docs/intellij/welcome.html)
- Uses DataGrip database APIs
- Thanks to all [contributors](https://github.com/mustache-cn/schema-lens/graphs/contributors)

---

## 📞 Contact & Links

- **GitHub**: [https://github.com/mustache-cn/schema-lens](https://github.com/mustache-cn/schema-lens)
- **Issues**: [https://github.com/mustache-cn/schema-lens/issues](https://github.com/mustache-cn/schema-lens/issues)
- **JetBrains Marketplace**: [Coming Soon](https://plugins.jetbrains.com)

---

<div align="center">

**Made with ❤️ for the DataGrip community**

⭐ Star this repo if you find it useful!

</div>
