package com.dodles.gdx.dodleengine.editor.inline;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.dodles.gdx.dodleengine.PerDodleEngine;
import com.dodles.gdx.dodleengine.assets.AssetProvider;
import com.dodles.gdx.dodleengine.assets.StringAssets;
import com.dodles.gdx.dodleengine.editor.AbstractEditorView;
import com.dodles.gdx.dodleengine.events.EngineEventManager;
import com.dodles.gdx.dodleengine.events.EngineEventType;
import com.dodles.gdx.dodleengine.events.EventBus;
import com.dodles.gdx.dodleengine.events.EventData;
import com.dodles.gdx.dodleengine.events.EventSubscriber;
import com.dodles.gdx.dodleengine.events.EventTopic;
import com.dodles.gdx.dodleengine.events.EventType;
import com.dodles.gdx.dodleengine.tools.ToolRegistry;
import com.dodles.gdx.dodleengine.tools.draw.DrawTool;
import com.dodles.gdx.dodleengine.tools.font.FontTool;
import com.github.czyzby.lml.parser.LmlParser;
import com.github.czyzby.lml.vis.util.VisLml;

import javax.inject.Inject;

/**
 * Tool configuration row for the inline editor.
 */
@PerDodleEngine
public class InlineToolConfigRow extends AbstractEditorView {
    private final AssetProvider assetProvider;
    private final EngineEventManager eventManager;
    private final EventBus eventBus;
    private final ToolRegistry toolRegistry;
    
    private Table rootTable;
    private TextButton toolButton;
    private TextButton colorButton;
    private TextButton strokeOpacityButton;
    private EventSubscriber toolChangedListener;
    private String curState;

    @Inject
    public InlineToolConfigRow(
            AssetProvider assetProvider,
            EngineEventManager eventManager,
            EventBus eventBus,
            ToolRegistry toolRegistry
    ) {
        this.assetProvider = assetProvider;
        this.eventManager = eventManager;
        this.eventBus = eventBus;
        this.toolRegistry = toolRegistry;
    }
    
    @Override
    public final void activate(Skin skin, String newState) {
        curState = newState;
        
        if (rootTable == null) {
            LmlParser parser = VisLml.parser()
                    .skin(skin)
                    .build();

            String template = assetProvider.getString(StringAssets.TEMPLATE_INLINE_TOOL_CONFIG_ROW);
            rootTable = (Table) parser.parseTemplate(template).get(0);
            rootTable.setFillParent(true);
            
            toolButton = configureButton("toolButton");
            toolButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    String toolName = getToolNameFromCurState();
                    eventManager.fireEvent(EngineEventType.CHANGE_EDITOR_STATE, toolName);
                }
            });
            
            colorButton = configureButton("colorButton");
            colorButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    String toolName = getToolNameFromCurState();
                    eventManager.fireEvent(EngineEventType.CHANGE_EDITOR_STATE, toolName +  ".color");
                }
            });
            
            strokeOpacityButton = configureButton("strokeButton");
            strokeOpacityButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    String toolName = getToolNameFromCurState();
                    eventManager.fireEvent(EngineEventType.CHANGE_EDITOR_STATE, toolName +  ".stroke");
                }
            });
            
            this.addActor(rootTable);
        }


        toolChangedListener = new EventSubscriber(EventTopic.DEFAULT) {
            @Override
            public void listen(EventTopic topic, EventType eventType, EventData data) {
                if (EventType.TOOL_CHANGED.equals(eventType)) {
                    configureToolButton();
                }
            }
        };
        
        eventBus.addSubscriber(toolChangedListener);
        configureToolButton();
    }

    @Override
    public final void deactivate() {
        eventBus.removeSubscriber(toolChangedListener);
    }

    @Override
    protected final WidgetGroup getRootWidget() {
        return rootTable;
    }
    
    private void configureToolButton() {
        String activeToolName = toolRegistry.getActiveTool().getName();
                
        if (activeToolName == DrawTool.TOOL_NAME) {
            toolButton.setText("Brush");
        } else if (activeToolName == FontTool.TOOL_NAME) {
            toolButton.setText("Font");
        }
    }
    
    private TextButton configureButton(String id) {
        TextButton button = rootTable.findActor(id);
        button.getLabel().setFontScale(0.5f);
        Table parentTable = (Table) button.getParent();
        parentTable.getCell(button).width(Value.percentWidth(1f / 3f, parentTable));
        return button;
    }
    
    private String getToolNameFromCurState() {
        String toolName = toolRegistry.getToolNameFromState(curState);
        
        if (toolName == null) {
            return curState;
        }
        
        return toolName;
    }
}
