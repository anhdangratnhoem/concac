package Note;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.File;

class Note {
    private String title;
    private String content;
    private LocalDateTime reminder;
    private String imagePath;  // Added imagePath field
    private List<Note> children;
    private Note parent;

    public Note(String title) {
        this.title = title;
        this.children = new ArrayList<>();
    }

    // Getters and Setters

    public void addChild(Note child) {
        child.setParent(this);
        children.add(child);
    }

    public void setParent(Note parent) {
        this.parent = parent;
    }

    public Note getParent() {
        return parent;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setReminder(LocalDateTime reminder) {
        this.reminder = reminder;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getTitle() {
        return title;
    }

    public List<Note> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return title;
    }
}

class NoteUtil {
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
}

public class NoteApp {
    private static Scanner scanner = new Scanner(System.in);
    private static Note rootNote = new Note("Root");

    public static void main(String[] args) {
        while (true) {
            printMenu();

            int choice = getIntInput("Enter your choice: ");

            switch (choice) {
                case 1:
                    createNote();
                    break;
                case 2:
                    editNote();
                    break;
                case 3:
                    deleteNote();
                    break;
                case 4:
                    displayNotes(rootNote, 0);
                    break;
                case 5:
                    System.out.println("Exiting the program.");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n1. Create Note");
        System.out.println("2. Edit Note");
        System.out.println("3. Delete Note");
        System.out.println("4. Display Notes");
        System.out.println("5. Exit");
    }

    private static void createNote() {
        System.out.print("Enter the title of the note: ");
        String title = scanner.nextLine();

        Note newNote = new Note(title);

        System.out.print("Enter the content of the note: ");
        newNote.setContent(scanner.nextLine());

        System.out.print("Do you want to add an image? (y/n): ");
        String addImage = scanner.nextLine();
        if (addImage.equalsIgnoreCase("y")) {
            String imagePath = getImagePath();
            newNote.setImagePath(imagePath);
        }

        System.out.print("Enter the reminder date and time (yyyy-MM-dd HH:mm): ");
        String reminderInput = scanner.nextLine();
        if (!reminderInput.isEmpty()) {
            try {
                newNote.setReminder(LocalDateTime.parse(reminderInput, NoteUtil.DATE_TIME_FORMATTER));
            } catch (Exception e) {
                System.out.println("Invalid date format. Note created without a reminder.");
            }
        }

        rootNote.addChild(newNote);
        System.out.println("Note created successfully!");
    }

    private static String getImagePath() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "gif");
        fileChooser.setFileFilter(filter);

        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            return selectedFile.getAbsolutePath();
        } else {
            return null;
        }
    }

    private static void editNote() {
        displayNotes(rootNote, 0);

        System.out.print("Enter the title of the note to edit: ");
        String noteTitleToEdit = scanner.nextLine();

        Note selectedNote = findNoteByTitle(rootNote, noteTitleToEdit);

        if (selectedNote != null) {
            System.out.print("Enter the new title of the note: ");
            selectedNote.setTitle(scanner.nextLine());

            System.out.print("Enter the new content of the note: ");
            selectedNote.setContent(scanner.nextLine());

            System.out.print("Enter the new reminder date and time (yyyy-MM-dd HH:mm): ");
            String reminderInput = scanner.nextLine();
            if (!reminderInput.isEmpty()) {
                selectedNote.setReminder(LocalDateTime.parse(reminderInput, NoteUtil.DATE_TIME_FORMATTER));
            }

            System.out.println("Note edited successfully!");
        } else {
            System.out.println("Invalid note index.");
        }
    }

    private static void deleteNote() {
        displayNotes(rootNote, 0);

        System.out.print("Enter the title of the note to delete: ");
        String noteTitleToDelete = scanner.nextLine();

        Note selectedNote = findNoteByTitle(rootNote, noteTitleToDelete);

        if (selectedNote != null) {
            Note parentNote = selectedNote.getParent();
            if (parentNote != null) {
                parentNote.getChildren().remove(selectedNote);
                System.out.println("Note deleted successfully!");
            } else {
                System.out.println("Cannot delete the root note.");
            }
        } else {
            System.out.println("Note with title '" + noteTitleToDelete + "' not found.");
        }
    }


    private static void displayNotes(Note note, int depth) {
        System.out.println(" ".repeat(depth * 2) + (depth > 0 ? "|_" : "") + note.getTitle());

        if (note.getChildren() != null) {
            for (Note child : note.getChildren()) {
                displayNotes(child, depth + 1);
            }
        }
    }

    private static int getIntInput(String prompt) {
        int input = 0;
        boolean validInput = false;

        do {
            try {
                System.out.print(prompt);
                input = Integer.parseInt(scanner.nextLine());
                validInput = true;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        } while (!validInput);

        return input;
    }

    private static Note findNoteByTitle(Note note, String title) {
        if (note.getTitle().equals(title)) {
            return note;
        } else {
            for (Note child : note.getChildren()) {
                Note foundNote = findNoteByTitle(child, title);
                if (foundNote != null) {
                    return foundNote;
                }
            }
            return null;
        }
    }
    private static void flattenNotes(Note note, List<Note> flattenedNotes) {
        flattenedNotes.add(note);

        if (note.getChildren() != null) {
            for (Note child : note.getChildren()) {
                flattenNotes(child, flattenedNotes);
            }
        }
    }
}
