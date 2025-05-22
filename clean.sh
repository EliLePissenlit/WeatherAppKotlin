#!/bin/bash

echo "🧹 Début du nettoyage..."

# Vérification et recréation du wrapper Gradle si nécessaire
if [ ! -f "gradle/wrapper/gradle-wrapper.jar" ] || [ ! -f "gradle/wrapper/gradle-wrapper.properties" ]; then
    echo "🔄 Recréation du wrapper Gradle..."
    mkdir -p gradle/wrapper
    curl -o gradle/wrapper/gradle-wrapper.jar https://raw.githubusercontent.com/gradle/gradle/master/gradle/wrapper/gradle-wrapper.jar
    curl -o gradle/wrapper/gradle-wrapper.properties https://raw.githubusercontent.com/gradle/gradle/master/gradle/wrapper/gradle-wrapper.properties
fi

# Nettoyage Gradle
./gradlew clean
./gradlew --stop

# Suppression des dossiers de build
echo "🗑️  Suppression des dossiers de build..."
rm -rf build/
rm -rf app/build/
rm -rf .gradle/

# Suppression des fichiers de configuration Android Studio
echo "🗑️  Suppression des fichiers de configuration..."
rm -rf .idea/
rm -f *.iml
rm -f app/*.iml

# Réinitialisation des permissions
echo "🔒 Réinitialisation des permissions..."
chmod +x gradlew

echo "✨ Nettoyage terminé !"
echo "📝 Pour reconstruire le projet, exécutez :"
echo "   ./gradlew clean build" 