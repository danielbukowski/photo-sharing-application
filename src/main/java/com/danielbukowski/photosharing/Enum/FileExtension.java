package com.danielbukowski.photosharing.Enum;

public enum FileExtension {
    JPEG,
    PNG;

    public static FileExtension getFromString(String string) {
        for (FileExtension fileExtension : FileExtension.values()) {
            if (fileExtension.name().equalsIgnoreCase(string)) return fileExtension;
        }
        throw new RuntimeException("Couldn't find an extension from the string: " + string);
    }
}
