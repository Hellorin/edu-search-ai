package io.hellorin.edusearchai.views.upload;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.P;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import io.hellorin.edusearchai.views.MainLayout;

import java.io.InputStream;

@PageTitle("Upload Documents")
@Route(value = "upload", layout = MainLayout.class)
public class UploadView extends VerticalLayout {

    private MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
    private Upload upload;
    private VerticalLayout uploadedFilesList;
    private ProgressBar progressBar;
    private int uploadedCount = 0;

    public UploadView() {
        setSpacing(false);
        setSizeFull();
        addClassName("upload-view");

        add(createHeader());
        add(createUploadSection());
        add(createUploadedFilesSection());
    }

    private Component createHeader() {
        VerticalLayout header = new VerticalLayout();
        header.setSpacing(false);
        header.addClassNames(LumoUtility.Padding.Horizontal.LARGE, LumoUtility.Padding.Top.LARGE);

        H1 title = new H1("Upload Educational Documents");
        title.addClassNames(LumoUtility.FontSize.XXXLARGE, LumoUtility.Margin.Bottom.NONE);
        
        P subtitle = new P("Upload PDFs, documents, and educational materials to enhance your knowledge base");
        subtitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.TextColor.SECONDARY);

        header.add(title, subtitle);
        return header;
    }

    private Component createUploadSection() {
        VerticalLayout section = new VerticalLayout();
        section.addClassNames(LumoUtility.Padding.Horizontal.LARGE, LumoUtility.Padding.Vertical.MEDIUM);

        // Document metadata form
        VerticalLayout metadataForm = createMetadataForm();
        
        // Upload component
        VerticalLayout uploadCard = createUploadCard();

        HorizontalLayout content = new HorizontalLayout();
        content.setWidthFull();
        content.setSpacing(true);
        content.add(uploadCard, metadataForm);
        content.setFlexGrow(1, uploadCard);
        content.setFlexGrow(1, metadataForm);

        section.add(content);
        return section;
    }

    private VerticalLayout createUploadCard() {
        VerticalLayout card = new VerticalLayout();
        card.addClassNames(
            LumoUtility.Background.CONTRAST_5,
            LumoUtility.BorderRadius.LARGE,
            LumoUtility.Padding.LARGE
        );

        H3 uploadTitle = new H3("Drop Files Here");
        uploadTitle.addClassName(LumoUtility.TextAlign.CENTER);

        upload = new Upload(buffer);
        upload.setAcceptedFileTypes("application/pdf", ".pdf", ".doc", ".docx", ".txt");
        upload.setMaxFiles(10);
        upload.setMaxFileSize(50 * 1024 * 1024); // 50MB

        // Custom upload area styling
        upload.setDropLabel(new Span("Drop educational documents here"));
        upload.setUploadButton(new Button("Choose Files", VaadinIcon.UPLOAD.create()));

        // Progress bar
        progressBar = new ProgressBar();
        progressBar.setVisible(false);
        progressBar.setWidthFull();

        upload.addSucceededListener(event -> {
            uploadedCount++;
            String fileName = event.getFileName();
            long contentLength = event.getContentLength();
            InputStream inputStream = buffer.getInputStream(fileName);
            
            // Here you would typically save the file and process it
            showSuccessNotification("File uploaded successfully: " + fileName);
            addUploadedFile(fileName, contentLength);
            
            progressBar.setVisible(false);
        });

        upload.addStartedListener(event -> {
            progressBar.setVisible(true);
            progressBar.setValue(0);
        });

        upload.addProgressListener(event -> {
            double progress = (double) event.getBytesReceived() / event.getContentLength();
            progressBar.setValue(progress);
        });

        upload.addFailedListener(event -> {
            showErrorNotification("Upload failed: " + event.getReason().getMessage());
            progressBar.setVisible(false);
        });

        card.add(uploadTitle, upload, progressBar);
        return card;
    }

    private VerticalLayout createMetadataForm() {
        VerticalLayout form = new VerticalLayout();
        form.addClassNames(
            LumoUtility.Background.CONTRAST_5,
            LumoUtility.BorderRadius.LARGE,
            LumoUtility.Padding.LARGE
        );

        H3 formTitle = new H3("Document Information");

        TextField titleField = new TextField("Document Title");
        titleField.setWidthFull();
        titleField.setPlaceholder("Enter a descriptive title...");

        Select<String> categorySelect = new Select<>();
        categorySelect.setLabel("Category");
        categorySelect.setWidthFull();
        categorySelect.setItems(
            "Mathematics", "Science", "Literature", "History", 
            "Computer Science", "Engineering", "Medicine", "Other"
        );
        categorySelect.setPlaceholder("Select category...");

        Select<String> levelSelect = new Select<>();
        levelSelect.setLabel("Education Level");
        levelSelect.setWidthFull();
        levelSelect.setItems(
            "Elementary", "High School", "Undergraduate", 
            "Graduate", "Professional", "Research"
        );
        levelSelect.setPlaceholder("Select level...");

        TextArea descriptionArea = new TextArea("Description");
        descriptionArea.setWidthFull();
        descriptionArea.setPlaceholder("Brief description of the document content...");
        descriptionArea.setHeight("120px");

        TextField tagsField = new TextField("Tags");
        tagsField.setWidthFull();
        tagsField.setPlaceholder("algebra, calculus, derivatives (comma-separated)");

        Button processButton = new Button("Process Documents", VaadinIcon.COG.create());
        processButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        processButton.setWidthFull();
        processButton.addClickListener(e -> {
            if (uploadedCount > 0) {
                processDocuments();
            } else {
                showErrorNotification("Please upload at least one document first");
            }
        });

        form.add(formTitle, titleField, categorySelect, levelSelect, descriptionArea, tagsField, processButton);
        return form;
    }

    private Component createUploadedFilesSection() {
        VerticalLayout section = new VerticalLayout();
        section.addClassNames(LumoUtility.Padding.Horizontal.LARGE, LumoUtility.Padding.Bottom.LARGE);

        H3 sectionTitle = new H3("Uploaded Files");
        sectionTitle.addClassNames(LumoUtility.Margin.Bottom.MEDIUM);

        uploadedFilesList = new VerticalLayout();
        uploadedFilesList.setSpacing(false);
        uploadedFilesList.addClassName(LumoUtility.Background.CONTRAST_5);
        uploadedFilesList.addClassName(LumoUtility.BorderRadius.LARGE);
        uploadedFilesList.addClassName(LumoUtility.Padding.MEDIUM);
        uploadedFilesList.setVisible(false);

        section.add(sectionTitle, uploadedFilesList);
        return section;
    }

    private void addUploadedFile(String fileName, long fileSize) {
        if (!uploadedFilesList.isVisible()) {
            uploadedFilesList.setVisible(true);
        }

        HorizontalLayout fileItem = new HorizontalLayout();
        fileItem.setWidthFull();
        fileItem.setAlignItems(FlexComponent.Alignment.CENTER);
        fileItem.addClassName(LumoUtility.Padding.Vertical.SMALL);

        Icon fileIcon = VaadinIcon.FILE_O.create();
        fileIcon.addClassName(LumoUtility.IconSize.MEDIUM);
        fileIcon.addClassName(LumoUtility.TextColor.SUCCESS);

        VerticalLayout fileInfo = new VerticalLayout();
        fileInfo.setSpacing(false);
        fileInfo.setPadding(false);

        Span fileNameSpan = new Span(fileName);
        fileNameSpan.addClassName(LumoUtility.FontWeight.MEDIUM);

        Span fileSizeSpan = new Span(formatFileSize(fileSize));
        fileSizeSpan.addClassName(LumoUtility.FontSize.SMALL);
        fileSizeSpan.addClassName(LumoUtility.TextColor.SECONDARY);

        fileInfo.add(fileNameSpan, fileSizeSpan);

        Icon statusIcon = VaadinIcon.CHECK_CIRCLE.create();
        statusIcon.addClassName(LumoUtility.IconSize.SMALL);
        statusIcon.addClassName(LumoUtility.TextColor.SUCCESS);

        fileItem.add(fileIcon, fileInfo, statusIcon);
        uploadedFilesList.add(fileItem);
    }

    private void processDocuments() {
        showInfoNotification("Processing " + uploadedCount + " documents...");
        // Here you would integrate with your document processing service
        // For now, just show a success message
        uploadedCount = 0;
        uploadedFilesList.removeAll();
        uploadedFilesList.setVisible(false);
        showSuccessNotification("Documents processed successfully and added to knowledge base!");
    }

    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
    }

    private void showSuccessNotification(String message) {
        Notification notification = Notification.show(message, 3000, Notification.Position.TOP_END);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void showErrorNotification(String message) {
        Notification notification = Notification.show(message, 5000, Notification.Position.TOP_END);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }

    private void showInfoNotification(String message) {
        Notification notification = Notification.show(message, 3000, Notification.Position.TOP_END);
        notification.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
    }
}