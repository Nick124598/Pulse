package org.nick124.FileChooser;

import java.io.File;
import java.util.Arrays;

import javax.swing.tree.DefaultMutableTreeNode;

public class CreateFileTree {
    public static DefaultMutableTreeNode createNode(File file) {

        DefaultMutableTreeNode node =
                new DefaultMutableTreeNode(file);

        File[] files = file.listFiles();

        if (files != null) {

            Arrays.sort(files, (a, b) -> {

                if (a.isDirectory() && !b.isDirectory()) {
					return -1;
				}

                if (!a.isDirectory() && b.isDirectory()) {
					return 1;
				}

                return a.getName().compareToIgnoreCase(b.getName());
            });

            for (File child : files) {
                node.add(createNode(child));
            }
        }

        return node;
    }
}
