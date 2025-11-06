package com.automobileproject.EAP.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    /**
     * Upload image to Cloudinary
     * @param file - the image file to upload
     * @param folder - folder name in Cloudinary (e.g., "services", "categories")
     * @return URL of the uploaded image
     */
    public String uploadImage(MultipartFile file, String folder) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty or null");
        }

        // Validate file type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("File must be an image");
        }

        // Upload with options
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap(
                        "folder", folder,
                        "resource_type", "image",
                        "transformation", new com.cloudinary.Transformation()
                                .width(800)
                                .height(600)
                                .crop("limit")
                                .quality("auto")
                                .fetchFormat("auto")
                ));

        return uploadResult.get("secure_url").toString();
    }

    /**
     * Delete image from Cloudinary
     * @param imageUrl - the URL of the image to delete
     */
    public void deleteImage(String imageUrl) throws IOException {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return;
        }

        // Extract public ID from URL
        String publicId = extractPublicIdFromUrl(imageUrl);
        if (publicId != null) {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        }
    }

    /**
     * Extract public ID from Cloudinary URL
     * Example URL: https://res.cloudinary.com/demo/image/upload/v1234567890/services/image.jpg
     * Public ID: services/image
     */
    private String extractPublicIdFromUrl(String imageUrl) {
        try {
            String[] parts = imageUrl.split("/upload/");
            if (parts.length > 1) {
                String afterUpload = parts[1];
                // Remove version number (v1234567890/)
                String withoutVersion = afterUpload.replaceFirst("v\\d+/", "");
                // Remove file extension
                int lastDot = withoutVersion.lastIndexOf('.');
                if (lastDot > 0) {
                    return withoutVersion.substring(0, lastDot);
                }
                return withoutVersion;
            }
        } catch (Exception e) {
            // If parsing fails, return null
            return null;
        }
        return null;
    }

    /**
     * Update image - delete old and upload new
     */
    public String updateImage(MultipartFile newFile, String oldImageUrl, String folder) throws IOException {
        // Delete old image if exists
        if (oldImageUrl != null && !oldImageUrl.isEmpty()) {
            try {
                deleteImage(oldImageUrl);
            } catch (Exception e) {
                // Log but don't fail if deletion fails
                System.err.println("Failed to delete old image: " + e.getMessage());
            }
        }

        // Upload new image
        return uploadImage(newFile, folder);
    }
}