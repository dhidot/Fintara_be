package com.sakuBCA.config.security;

import org.springframework.stereotype.Component;

@Component
public class NameNormalizer {

    // Method untuk menormalkan nama cabang
    public String normalizedName(String name) {
        // Menghapus spasi yang tidak perlu di awal dan akhir
        name = name.trim();

        // Jika nama terdiri lebih dari satu kata
        if (name.contains(" ")) {
            String[] words = name.split(" ");
            StringBuilder normalized = new StringBuilder();

            for (String word : words) {
                // Mengubah huruf pertama menjadi kapital dan sisanya menjadi huruf kecil
                normalized.append(word.substring(0, 1).toUpperCase())
                        .append(word.substring(1).toLowerCase())
                        .append(" ");
            }

            // Menghapus spasi ekstra di akhir
            return normalized.toString().trim();
        }

        // Jika nama hanya satu kata
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }

    public String normalizeRoleName(String name) {
        // jadi kapital semua dan jika terdiri dari lebih 1 kata kasih underscore
        name = name.toUpperCase();
        if (name.contains(" ")) {
            String[] words = name.split(" ");
            StringBuilder normalized = new StringBuilder();

            for (String word : words) {
                normalized.append(word).append("_");
            }

            // Menghapus underscore ekstra di akhir
            return normalized.toString().substring(0, normalized.length() - 1);
        }
        // Jika nama hanya satu kata
        return name;
    }
}

