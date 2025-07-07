package io.hellorin.edusearchai.views.dashboard;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.P;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import io.hellorin.edusearchai.views.MainLayout;
import io.hellorin.edusearchai.views.search.SearchView;
import io.hellorin.edusearchai.views.upload.UploadView;

@PageTitle("Dashboard")
@Route(value = "", layout = MainLayout.class)
public class DashboardView extends VerticalLayout {

    public DashboardView() {
        setSpacing(false);
        setSizeFull();
        addClassName("dashboard-view");

        add(createHeader());
        add(createStatsSection());
        add(createQuickActionsSection());
        add(createRecentActivitySection());
    }

    private Component createHeader() {
        VerticalLayout header = new VerticalLayout();
        header.setSpacing(false);
        header.addClassNames(LumoUtility.Padding.Horizontal.LARGE, LumoUtility.Padding.Top.LARGE);

        H1 title = new H1("Welcome to EduSearch AI");
        title.addClassNames(LumoUtility.FontSize.XXXLARGE, LumoUtility.Margin.Bottom.NONE);
        
        P subtitle = new P("Your intelligent educational content search and management platform");
        subtitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.TextColor.SECONDARY);

        header.add(title, subtitle);
        return header;
    }

    private Component createStatsSection() {
        Board board = new Board();
        board.addClassNames(LumoUtility.Padding.Horizontal.LARGE, LumoUtility.Padding.Vertical.MEDIUM);

        board.addRow(
            createStatCard("Documents", "1,247", VaadinIcon.BOOK, "success"),
            createStatCard("Knowledge Queries", "3,891", VaadinIcon.SEARCH, "primary"),
            createStatCard("Active Users", "156", VaadinIcon.USERS, "contrast"),
            createStatCard("Processing Queue", "23", VaadinIcon.CLOCK, "error")
        );

        return board;
    }

    private Component createStatCard(String title, String value, VaadinIcon iconType, String theme) {
        VerticalLayout card = new VerticalLayout();
        card.addClassNames(
            LumoUtility.Background.CONTRAST_5,
            LumoUtility.BorderRadius.LARGE,
            LumoUtility.Padding.LARGE
        );
        card.setSpacing(false);

        HorizontalLayout header = new HorizontalLayout();
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setWidthFull();

        Icon icon = iconType.create();
        icon.addClassNames(LumoUtility.IconSize.LARGE);
        icon.getStyle().set("color", "var(--lumo-" + theme + "-color)");

        H3 cardTitle = new H3(title);
        cardTitle.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY, LumoUtility.Margin.NONE);

        VerticalLayout content = new VerticalLayout();
        content.setSpacing(false);
        content.setPadding(false);

        H2 cardValue = new H2(value);
        cardValue.addClassNames(LumoUtility.FontSize.XXXLARGE, LumoUtility.Margin.NONE);

        content.add(cardTitle, cardValue);
        header.add(content, icon);
        card.add(header);

        return card;
    }

    private Component createQuickActionsSection() {
        VerticalLayout section = new VerticalLayout();
        section.addClassNames(LumoUtility.Padding.Horizontal.LARGE);

        H3 sectionTitle = new H3("Quick Actions");
        sectionTitle.addClassNames(LumoUtility.Margin.Bottom.MEDIUM);

        HorizontalLayout actions = new HorizontalLayout();
        actions.setWidthFull();
        actions.setSpacing(true);

        Button uploadBtn = new Button("Upload Documents", VaadinIcon.UPLOAD.create());
        uploadBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        uploadBtn.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(UploadView.class)));

        Button searchBtn = new Button("Search Knowledge", VaadinIcon.SEARCH.create());
        searchBtn.addThemeVariants(ButtonVariant.LUMO_LARGE);
        searchBtn.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(SearchView.class)));

        Button analyzeBtn = new Button("View Analytics", VaadinIcon.CHART.create());
        analyzeBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_LARGE);

        actions.add(uploadBtn, searchBtn, analyzeBtn);

        section.add(sectionTitle, actions);
        return section;
    }

    private Component createRecentActivitySection() {
        VerticalLayout section = new VerticalLayout();
        section.addClassNames(LumoUtility.Padding.Horizontal.LARGE, LumoUtility.Padding.Bottom.LARGE);

        H3 sectionTitle = new H3("Recent Activity");
        sectionTitle.addClassNames(LumoUtility.Margin.Bottom.MEDIUM);

        VerticalLayout activityList = new VerticalLayout();
        activityList.setSpacing(false);
        activityList.addClassName(LumoUtility.Background.CONTRAST_5);
        activityList.addClassName(LumoUtility.BorderRadius.LARGE);
        activityList.addClassName(LumoUtility.Padding.MEDIUM);

        activityList.add(
            createActivityItem("Document uploaded: Advanced Mathematics.pdf", "2 minutes ago", VaadinIcon.UPLOAD),
            createActivityItem("Knowledge search: 'Linear Algebra concepts'", "15 minutes ago", VaadinIcon.SEARCH),
            createActivityItem("Document processed: Physics Laboratory Manual.pdf", "1 hour ago", VaadinIcon.COG),
            createActivityItem("New user registered: student@university.edu", "3 hours ago", VaadinIcon.USER)
        );

        section.add(sectionTitle, activityList);
        return section;
    }

    private Component createActivityItem(String activity, String time, VaadinIcon iconType) {
        HorizontalLayout item = new HorizontalLayout();
        item.setWidthFull();
        item.setAlignItems(FlexComponent.Alignment.CENTER);
        item.addClassName(LumoUtility.Padding.Vertical.SMALL);

        Icon icon = iconType.create();
        icon.addClassName(LumoUtility.IconSize.SMALL);
        icon.addClassName(LumoUtility.TextColor.SECONDARY);

        VerticalLayout content = new VerticalLayout();
        content.setSpacing(false);
        content.setPadding(false);

        Span activityText = new Span(activity);
        Span timeText = new Span(time);
        timeText.addClassName(LumoUtility.FontSize.SMALL);
        timeText.addClassName(LumoUtility.TextColor.TERTIARY);

        content.add(activityText, timeText);

        item.add(icon, content);
        return item;
    }
}