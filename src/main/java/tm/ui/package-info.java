/**
 * Swing UI for Tile Molester.
 * <p>
 * {@link tm.ui.TMUI} is the main frame. Command handlers ({@code TMUI*Actions}),
 * menus, and toolbars live in this package because they share package-private access
 * with {@link tm.ui.TMUIWidgets}. Reusable pieces are in subpackages:
 * <ul>
 *   <li>{@code tm.ui.view} — MDI document views</li>
 *   <li>{@code tm.ui.widget} — palette pane, status bar, tool buttons, progress dialog</li>
 *   <li>{@code tm.ui.menu} — dynamic menu item types</li>
 *   <li>{@code tm.ui.filter} — file chooser filters</li>
 *   <li>{@code tm.ui.settings} — persisted settings and look-and-feel</li>
 * </ul>
 */
package tm.ui;
