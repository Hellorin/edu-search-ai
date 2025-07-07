package io.hellorin.edusearchai.views.search;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.P;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import io.hellorin.edusearchai.views.MainLayout;

import java.util.ArrayList;
import java.util.List;

@PageTitle("Search Knowledge")
@Route(value = "search", layout = MainLayout.class)
public class SearchView extends VerticalLayout {

    private TextField searchField;
    private Select<String> searchTypeSelect;
    private Select<String> categoryFilter;
    private VerticalLayout resultsContainer;
    private ProgressBar searchProgress;
    private boolean isSearching = false;

    public SearchView() {
        setSpacing(false);
        setSizeFull();
        addClassName("search-view");

        add(createHeader());
        add(createSearchSection());
        add(createResultsSection());
    }

    private Component createHeader() {
        VerticalLayout header = new VerticalLayout();
        header.setSpacing(false);
        header.addClassNames(LumoUtility.Padding.Horizontal.LARGE, LumoUtility.Padding.Top.LARGE);

        H1 title = new H1("Search Educational Knowledge");
        title.addClassNames(LumoUtility.FontSize.XXXLARGE, LumoUtility.Margin.Bottom.NONE);
        
        P subtitle = new P("Use AI-powered search to find relevant educational content from your knowledge base");
        subtitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.TextColor.SECONDARY);

        header.add(title, subtitle);
        return header;
    }

    private Component createSearchSection() {
        VerticalLayout section = new VerticalLayout();
        section.addClassNames(LumoUtility.Padding.Horizontal.LARGE, LumoUtility.Padding.Vertical.MEDIUM);

        VerticalLayout searchCard = new VerticalLayout();
        searchCard.addClassNames(
            LumoUtility.Background.CONTRAST_5,
            LumoUtility.BorderRadius.LARGE,
            LumoUtility.Padding.LARGE
        );

        // Search input row
        HorizontalLayout searchRow = new HorizontalLayout();
        searchRow.setWidthFull();
        searchRow.setAlignItems(FlexComponent.Alignment.END);

        searchField = new TextField();
        searchField.setPlaceholder("Ask any educational question or search for specific topics...");
        searchField.setWidthFull();
        searchField.addClassName(LumoUtility.FontSize.MEDIUM);
        searchField.addValueChangeListener(e -> {
            if (e.isFromClient() && e.getValue().length() > 0) {
                searchField.addThemeVariants();
            }
        });

        Button searchButton = new Button("Search", VaadinIcon.SEARCH.create());
        searchButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        searchButton.addClickListener(e -> performSearch());

        Button voiceButton = new Button(VaadinIcon.MICROPHONE.create());
        voiceButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        voiceButton.setTooltipText("Voice search (coming soon)");

        searchRow.add(searchField, voiceButton, searchButton);
        searchRow.setFlexGrow(1, searchField);

        // Filters row
        HorizontalLayout filtersRow = new HorizontalLayout();
        filtersRow.setWidthFull();
        filtersRow.setSpacing(true);

        searchTypeSelect = new Select<>();
        searchTypeSelect.setLabel("Search Type");
        searchTypeSelect.setItems("Semantic Search", "Keyword Search", "Q&A Mode", "Concept Exploration");
        searchTypeSelect.setValue("Semantic Search");
        searchTypeSelect.setWidth("200px");

        categoryFilter = new Select<>();
        categoryFilter.setLabel("Category");
        categoryFilter.setItems("All Categories", "Mathematics", "Science", "Literature", "History", 
                              "Computer Science", "Engineering", "Medicine");
        categoryFilter.setValue("All Categories");
        categoryFilter.setWidth("200px");

        Select<String> levelFilter = new Select<>();
        levelFilter.setLabel("Level");
        levelFilter.setItems("All Levels", "Elementary", "High School", "Undergraduate", 
                           "Graduate", "Professional", "Research");
        levelFilter.setValue("All Levels");
        levelFilter.setWidth("200px");

        filtersRow.add(searchTypeSelect, categoryFilter, levelFilter);

        // Progress bar
        searchProgress = new ProgressBar();
        searchProgress.setIndeterminate(true);
        searchProgress.setVisible(false);
        searchProgress.setWidthFull();

        // Quick search suggestions
        HorizontalLayout suggestionsRow = createQuickSuggestions();

        searchCard.add(searchRow, filtersRow, searchProgress, suggestionsRow);
        section.add(searchCard);
        return section;
    }

    private HorizontalLayout createQuickSuggestions() {
        HorizontalLayout suggestions = new HorizontalLayout();
        suggestions.setWidthFull();
        suggestions.setSpacing(true);
        suggestions.addClassName(LumoUtility.Margin.Top.MEDIUM);

        Span label = new Span("Try:");
        label.addClassName(LumoUtility.FontSize.SMALL);
        label.addClassName(LumoUtility.TextColor.SECONDARY);

        String[] quickSearches = {
            "Linear algebra basics", 
            "Quantum physics principles", 
            "Machine learning algorithms",
            "Calculus derivatives"
        };

        for (String search : quickSearches) {
            Button suggestionBtn = new Button(search);
            suggestionBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
            suggestionBtn.addClickListener(e -> {
                searchField.setValue(search);
                performSearch();
            });
            suggestions.add(suggestionBtn);
        }

        suggestions.setAlignItems(FlexComponent.Alignment.CENTER);
        return suggestions;
    }

    private Component createResultsSection() {
        VerticalLayout section = new VerticalLayout();
        section.addClassNames(LumoUtility.Padding.Horizontal.LARGE, LumoUtility.Padding.Bottom.LARGE);

        H3 resultsTitle = new H3("Search Results");
        resultsTitle.setVisible(false);
        resultsTitle.addClassName("results-title");

        resultsContainer = new VerticalLayout();
        resultsContainer.setSpacing(true);
        resultsContainer.setVisible(false);

        section.add(resultsTitle, resultsContainer);
        return section;
    }

    private void performSearch() {
        String query = searchField.getValue().trim();
        if (query.isEmpty()) {
            return;
        }

        if (isSearching) {
            return;
        }

        isSearching = true;
        searchProgress.setVisible(true);
        resultsContainer.setVisible(false);
        
        // Clear previous results
        resultsContainer.removeAll();

        // Simulate AI search with delay
        getUI().ifPresent(ui -> {
            ui.access(() -> {
                // Simulate processing time
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                // Generate mock results
                List<SearchResult> results = generateMockResults(query);
                displayResults(results);
                
                searchProgress.setVisible(false);
                isSearching = false;
                
                // Show results title
                section.getComponentAt(0).setVisible(true);
                resultsContainer.setVisible(true);
            });
        });
    }

    private List<SearchResult> generateMockResults(String query) {
        List<SearchResult> results = new ArrayList<>();
        
        // Generate some realistic mock results based on the query
        results.add(new SearchResult(
            "Introduction to " + query,
            "This comprehensive guide covers the fundamental concepts of " + query + " with practical examples and exercises.",
            "Advanced Mathematics Textbook",
            "Chapter 5, Pages 127-145",
            "Undergraduate",
            0.95f
        ));
        
        results.add(new SearchResult(
            "Practical Applications of " + query,
            "Learn how " + query + " is applied in real-world scenarios through case studies and problem-solving approaches.",
            "Engineering Applications Manual",
            "Section 3.2, Pages 78-92",
            "Graduate",
            0.87f
        ));
        
        results.add(new SearchResult(
            "Historical Development of " + query,
            "Explore the historical evolution and key milestones in the development of " + query + " theory.",
            "History of Mathematics",
            "Chapter 12, Pages 234-251",
            "Professional",
            0.78f
        ));

        return results;
    }

    private void displayResults(List<SearchResult> results) {
        for (SearchResult result : results) {
            Component resultCard = createResultCard(result);
            resultsContainer.add(resultCard);
        }
    }

    private Component createResultCard(SearchResult result) {
        VerticalLayout card = new VerticalLayout();
        card.addClassNames(
            LumoUtility.Background.CONTRAST_5,
            LumoUtility.BorderRadius.MEDIUM,
            LumoUtility.Padding.MEDIUM,
            LumoUtility.Border.ALL,
            "result-card"
        );
        card.setSpacing(false);

        // Header with relevance score
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        H4 title = new H4(result.title);
        title.addClassName(LumoUtility.Margin.NONE);
        title.addClassName(LumoUtility.TextColor.PRIMARY);

        // Relevance indicator
        HorizontalLayout relevanceLayout = new HorizontalLayout();
        relevanceLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        
        ProgressBar relevanceBar = new ProgressBar();
        relevanceBar.setValue(result.relevanceScore);
        relevanceBar.setWidth("60px");
        
        Span relevanceText = new Span(Math.round(result.relevanceScore * 100) + "%");
        relevanceText.addClassName(LumoUtility.FontSize.SMALL);
        relevanceText.addClassName(LumoUtility.TextColor.SECONDARY);
        
        relevanceLayout.add(relevanceBar, relevanceText);

        header.add(title, relevanceLayout);

        // Description
        P description = new P(result.description);
        description.addClassName(LumoUtility.Margin.Vertical.SMALL);

        // Metadata
        HorizontalLayout metadata = new HorizontalLayout();
        metadata.setSpacing(true);
        metadata.setAlignItems(FlexComponent.Alignment.CENTER);

        Icon sourceIcon = VaadinIcon.BOOK.create();
        sourceIcon.addClassName(LumoUtility.IconSize.SMALL);
        sourceIcon.addClassName(LumoUtility.TextColor.TERTIARY);

        Span source = new Span(result.source);
        source.addClassName(LumoUtility.FontSize.SMALL);
        source.addClassName(LumoUtility.FontWeight.MEDIUM);

        Span location = new Span("• " + result.location);
        location.addClassName(LumoUtility.FontSize.SMALL);
        location.addClassName(LumoUtility.TextColor.SECONDARY);

        Span level = new Span("• " + result.level);
        level.addClassName(LumoUtility.FontSize.SMALL);
        level.addClassName(LumoUtility.TextColor.TERTIARY);

        metadata.add(sourceIcon, source, location, level);

        // Actions
        HorizontalLayout actions = new HorizontalLayout();
        actions.setSpacing(true);
        actions.addClassName(LumoUtility.Margin.Top.SMALL);

        Button viewButton = new Button("View Document", VaadinIcon.EYE.create());
        viewButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);

        Button bookmarkButton = new Button(VaadinIcon.BOOKMARK.create());
        bookmarkButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        bookmarkButton.setTooltipText("Bookmark this result");

        Button shareButton = new Button(VaadinIcon.SHARE.create());
        shareButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        shareButton.setTooltipText("Share this result");

        actions.add(viewButton, bookmarkButton, shareButton);

        card.add(header, description, metadata, actions);
        return card;
    }

    // Inner class for search results
    private static class SearchResult {
        String title;
        String description;
        String source;
        String location;
        String level;
        float relevanceScore;

        SearchResult(String title, String description, String source, String location, String level, float relevanceScore) {
            this.title = title;
            this.description = description;
            this.source = source;
            this.location = location;
            this.level = level;
            this.relevanceScore = relevanceScore;
        }
    }
}