package io.hellorin.edusearchai.views.documents;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.P;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import io.hellorin.edusearchai.views.MainLayout;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@PageTitle("Document Library")
@Route(value = "documents", layout = MainLayout.class)
public class DocumentsView extends VerticalLayout {

    private Grid<Document> documentsGrid;
    private TextField searchField;
    private Select<String> categoryFilter;
    private Select<String> statusFilter;
    private List<Document> allDocuments;

    public DocumentsView() {
        setSpacing(false);
        setSizeFull();
        addClassName("documents-view");

        // Initialize data
        allDocuments = generateMockDocuments();

        add(createHeader());
        add(createFiltersSection());
        add(createDocumentsGrid());
    }

    private Component createHeader() {
        VerticalLayout header = new VerticalLayout();
        header.setSpacing(false);
        header.addClassNames(LumoUtility.Padding.Horizontal.LARGE, LumoUtility.Padding.Top.LARGE);

        H1 title = new H1("Document Library");
        title.addClassNames(LumoUtility.FontSize.XXXLARGE, LumoUtility.Margin.Bottom.NONE);
        
        P subtitle = new P("Manage and explore your educational document collection");
        subtitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.TextColor.SECONDARY);

        // Stats row
        HorizontalLayout stats = createStatsRow();

        header.add(title, subtitle, stats);
        return header;
    }

    private HorizontalLayout createStatsRow() {
        HorizontalLayout stats = new HorizontalLayout();
        stats.setSpacing(true);
        stats.addClassName(LumoUtility.Margin.Top.MEDIUM);

        long totalDocs = allDocuments.size();
        long processedDocs = allDocuments.stream().filter(d -> "Processed".equals(d.getStatus())).count();
        long pendingDocs = allDocuments.stream().filter(d -> "Processing".equals(d.getStatus())).count();

        stats.add(
            createStatBadge("Total Documents", String.valueOf(totalDocs), VaadinIcon.BOOK, "contrast"),
            createStatBadge("Processed", String.valueOf(processedDocs), VaadinIcon.CHECK_CIRCLE, "success"),
            createStatBadge("Processing", String.valueOf(pendingDocs), VaadinIcon.CLOCK, "error")
        );

        return stats;
    }

    private Component createStatBadge(String label, String value, VaadinIcon icon, String theme) {
        HorizontalLayout badge = new HorizontalLayout();
        badge.setAlignItems(FlexComponent.Alignment.CENTER);
        badge.setSpacing(false);
        badge.addClassNames(
            LumoUtility.Background.CONTRAST_5,
            LumoUtility.BorderRadius.MEDIUM,
            LumoUtility.Padding.Horizontal.MEDIUM,
            LumoUtility.Padding.Vertical.SMALL
        );

        Icon badgeIcon = icon.create();
        badgeIcon.addClassName(LumoUtility.IconSize.SMALL);
        badgeIcon.getStyle().set("color", "var(--lumo-" + theme + "-color)");

        Span badgeValue = new Span(value);
        badgeValue.addClassName(LumoUtility.FontWeight.BOLD);

        Span badgeLabel = new Span(label);
        badgeLabel.addClassName(LumoUtility.FontSize.SMALL);
        badgeLabel.addClassName(LumoUtility.TextColor.SECONDARY);

        VerticalLayout content = new VerticalLayout();
        content.setSpacing(false);
        content.setPadding(false);
        content.add(badgeValue, badgeLabel);

        badge.add(badgeIcon, content);
        return badge;
    }

    private Component createFiltersSection() {
        VerticalLayout section = new VerticalLayout();
        section.addClassNames(LumoUtility.Padding.Horizontal.LARGE, LumoUtility.Padding.Vertical.MEDIUM);

        HorizontalLayout filtersCard = new HorizontalLayout();
        filtersCard.setWidthFull();
        filtersCard.setAlignItems(FlexComponent.Alignment.END);
        filtersCard.setSpacing(true);
        filtersCard.addClassNames(
            LumoUtility.Background.CONTRAST_5,
            LumoUtility.BorderRadius.LARGE,
            LumoUtility.Padding.LARGE
        );

        // Search field
        searchField = new TextField();
        searchField.setPlaceholder("Search documents...");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        searchField.addValueChangeListener(e -> filterDocuments());
        searchField.setWidth("300px");

        // Category filter
        categoryFilter = new Select<>();
        categoryFilter.setLabel("Category");
        categoryFilter.setItems("All Categories", "Mathematics", "Science", "Literature", "History", 
                              "Computer Science", "Engineering", "Medicine", "Other");
        categoryFilter.setValue("All Categories");
        categoryFilter.addValueChangeListener(e -> filterDocuments());
        categoryFilter.setWidth("200px");

        // Status filter
        statusFilter = new Select<>();
        statusFilter.setLabel("Status");
        statusFilter.setItems("All Statuses", "Processed", "Processing", "Failed");
        statusFilter.setValue("All Statuses");
        statusFilter.addValueChangeListener(e -> filterDocuments());
        statusFilter.setWidth("150px");

        // Action buttons
        Button refreshButton = new Button("Refresh", VaadinIcon.REFRESH.create());
        refreshButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        Button exportButton = new Button("Export", VaadinIcon.DOWNLOAD.create());
        exportButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        filtersCard.add(searchField, categoryFilter, statusFilter, refreshButton, exportButton);
        filtersCard.setFlexGrow(1, searchField);

        section.add(filtersCard);
        return section;
    }

    private Component createDocumentsGrid() {
        VerticalLayout section = new VerticalLayout();
        section.addClassNames(LumoUtility.Padding.Horizontal.LARGE, LumoUtility.Padding.Bottom.LARGE);

        documentsGrid = new Grid<>(Document.class, false);
        documentsGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_WRAP_CELL_CONTENT);
        documentsGrid.setHeight("600px");

        // Configure columns
        documentsGrid.addColumn(createDocumentRenderer())
            .setHeader("Document")
            .setFlexGrow(3)
            .setSortable(true);

        documentsGrid.addColumn(Document::getCategory)
            .setHeader("Category")
            .setWidth("120px")
            .setSortable(true);

        documentsGrid.addColumn(Document::getLevel)
            .setHeader("Level")
            .setWidth("120px")
            .setSortable(true);

        documentsGrid.addColumn(createStatusRenderer())
            .setHeader("Status")
            .setWidth("120px")
            .setSortable(true);

        documentsGrid.addColumn(Document::getSize)
            .setHeader("Size")
            .setWidth("100px")
            .setSortable(true);

        documentsGrid.addColumn(createDateRenderer())
            .setHeader("Upload Date")
            .setWidth("150px")
            .setSortable(true);

        documentsGrid.addColumn(createActionsRenderer())
            .setHeader("Actions")
            .setWidth("180px")
            .setFlexGrow(0);

        documentsGrid.setItems(allDocuments);

        section.add(documentsGrid);
        return section;
    }

    private ComponentRenderer<HorizontalLayout, Document> createDocumentRenderer() {
        return new ComponentRenderer<>(document -> {
            HorizontalLayout layout = new HorizontalLayout();
            layout.setAlignItems(FlexComponent.Alignment.CENTER);
            layout.setSpacing(true);

            Icon fileIcon = getFileIcon(document.getType());
            fileIcon.addClassName(LumoUtility.IconSize.MEDIUM);

            VerticalLayout info = new VerticalLayout();
            info.setSpacing(false);
            info.setPadding(false);

            Span title = new Span(document.getTitle());
            title.addClassName(LumoUtility.FontWeight.MEDIUM);

            Span filename = new Span(document.getFilename());
            filename.addClassName(LumoUtility.FontSize.SMALL);
            filename.addClassName(LumoUtility.TextColor.SECONDARY);

            info.add(title, filename);
            layout.add(fileIcon, info);

            return layout;
        });
    }

    private ComponentRenderer<Span, Document> createStatusRenderer() {
        return new ComponentRenderer<>(document -> {
            Span status = new Span(document.getStatus());
            status.addClassName(LumoUtility.FontSize.SMALL);
            status.addClassName(LumoUtility.Padding.Horizontal.SMALL);
            status.addClassName(LumoUtility.Padding.Vertical.XSMALL);
            status.addClassName(LumoUtility.BorderRadius.MEDIUM);

            switch (document.getStatus()) {
                case "Processed":
                    status.addClassName(LumoUtility.Background.SUCCESS_10);
                    status.addClassName(LumoUtility.TextColor.SUCCESS);
                    break;
                case "Processing":
                    status.addClassName(LumoUtility.Background.CONTRAST_10);
                    status.addClassName(LumoUtility.TextColor.BODY);
                    break;
                case "Failed":
                    status.addClassName(LumoUtility.Background.ERROR_10);
                    status.addClassName(LumoUtility.TextColor.ERROR);
                    break;
            }

            return status;
        });
    }

    private ComponentRenderer<Span, Document> createDateRenderer() {
        return new ComponentRenderer<>(document -> {
            Span date = new Span(document.getUploadDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
            date.addClassName(LumoUtility.FontSize.SMALL);
            return date;
        });
    }

    private ComponentRenderer<HorizontalLayout, Document> createActionsRenderer() {
        return new ComponentRenderer<>(document -> {
            HorizontalLayout actions = new HorizontalLayout();
            actions.setSpacing(false);

            Button viewButton = new Button(VaadinIcon.EYE.create());
            viewButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_SMALL);
            viewButton.setTooltipText("View document");

            Button downloadButton = new Button(VaadinIcon.DOWNLOAD.create());
            downloadButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_SMALL);
            downloadButton.setTooltipText("Download");

            Button editButton = new Button(VaadinIcon.EDIT.create());
            editButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_SMALL);
            editButton.setTooltipText("Edit metadata");

            Button deleteButton = new Button(VaadinIcon.TRASH.create());
            deleteButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);
            deleteButton.setTooltipText("Delete document");

            actions.add(viewButton, downloadButton, editButton, deleteButton);
            return actions;
        });
    }

    private Icon getFileIcon(String type) {
        switch (type.toLowerCase()) {
            case "pdf":
                return VaadinIcon.FILE_TEXT.create();
            case "doc":
            case "docx":
                return VaadinIcon.FILE_TEXT_O.create();
            case "txt":
                return VaadinIcon.FILE_O.create();
            default:
                return VaadinIcon.FILE.create();
        }
    }

    private void filterDocuments() {
        String searchTerm = searchField.getValue().toLowerCase().trim();
        String selectedCategory = categoryFilter.getValue();
        String selectedStatus = statusFilter.getValue();

        List<Document> filteredDocuments = allDocuments.stream()
            .filter(doc -> {
                boolean matchesSearch = searchTerm.isEmpty() || 
                    doc.getTitle().toLowerCase().contains(searchTerm) ||
                    doc.getFilename().toLowerCase().contains(searchTerm);
                
                boolean matchesCategory = "All Categories".equals(selectedCategory) || 
                    doc.getCategory().equals(selectedCategory);
                
                boolean matchesStatus = "All Statuses".equals(selectedStatus) || 
                    doc.getStatus().equals(selectedStatus);
                
                return matchesSearch && matchesCategory && matchesStatus;
            })
            .collect(Collectors.toList());

        documentsGrid.setItems(filteredDocuments);
    }

    private List<Document> generateMockDocuments() {
        List<Document> documents = new ArrayList<>();
        
        documents.add(new Document("Advanced Calculus Textbook", "calculus_advanced.pdf", "Mathematics", 
            "Undergraduate", "Processed", "15.2 MB", "pdf", LocalDateTime.now().minusDays(5)));
        
        documents.add(new Document("Quantum Physics Principles", "quantum_physics.pdf", "Science", 
            "Graduate", "Processed", "23.8 MB", "pdf", LocalDateTime.now().minusDays(3)));
        
        documents.add(new Document("Machine Learning Algorithms", "ml_algorithms.docx", "Computer Science", 
            "Graduate", "Processing", "8.5 MB", "docx", LocalDateTime.now().minusDays(1)));
        
        documents.add(new Document("Linear Algebra Fundamentals", "linear_algebra.pdf", "Mathematics", 
            "Undergraduate", "Processed", "12.1 MB", "pdf", LocalDateTime.now().minusDays(7)));
        
        documents.add(new Document("Organic Chemistry Lab Manual", "organic_chem_lab.pdf", "Science", 
            "Undergraduate", "Failed", "31.5 MB", "pdf", LocalDateTime.now().minusDays(2)));
        
        documents.add(new Document("Software Engineering Practices", "software_eng.pdf", "Computer Science", 
            "Professional", "Processed", "18.9 MB", "pdf", LocalDateTime.now().minusDays(4)));
        
        documents.add(new Document("Statistical Analysis Methods", "statistics.docx", "Mathematics", 
            "Graduate", "Processing", "6.7 MB", "docx", LocalDateTime.now().minusHours(6)));

        return documents;
    }

    // Document model class
    public static class Document {
        private String title;
        private String filename;
        private String category;
        private String level;
        private String status;
        private String size;
        private String type;
        private LocalDateTime uploadDate;

        public Document(String title, String filename, String category, String level, 
                       String status, String size, String type, LocalDateTime uploadDate) {
            this.title = title;
            this.filename = filename;
            this.category = category;
            this.level = level;
            this.status = status;
            this.size = size;
            this.type = type;
            this.uploadDate = uploadDate;
        }

        // Getters
        public String getTitle() { return title; }
        public String getFilename() { return filename; }
        public String getCategory() { return category; }
        public String getLevel() { return level; }
        public String getStatus() { return status; }
        public String getSize() { return size; }
        public String getType() { return type; }
        public LocalDateTime getUploadDate() { return uploadDate; }
    }
}