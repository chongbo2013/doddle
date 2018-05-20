package com.dodles.gdx.dodleengine.assets;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.io.InputStream;

/**
 * An asset loader implementation to load raw string data.
 */
public class StringLoader extends SynchronousAssetLoader<String, StringLoader.Parameters> {
    public StringLoader(FileHandleResolver resolver) {
        super(resolver);
    }
    
    @Override
    public final String load(AssetManager assetManager, String fileName, FileHandle file, Parameters parameter) {
        byte[] buffer = new byte[1024];
        StringBuilder sb = new StringBuilder();
        int bytesRead;
        InputStream in = file.read();
        
        try {
            try {
                while ((bytesRead = in.read(buffer, 0, buffer.length)) > 0) {
                    if (bytesRead > 0) {
                        sb.append(new String(buffer, 0, bytesRead, "UTF-8"));
                    }
                }
            } finally {
                in.close();
            }
        } catch (Exception e) {
            throw new GdxRuntimeException("Error loading file " + fileName, e);
        }
        
        return sb.toString();
    }

    @Override
    public final Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, Parameters parameter) {
        return null;
    }
    
    /**
     * We're not using this, but it's needed for the generic definition.
     */
    public class Parameters extends AssetLoaderParameters<String> {
    }
}
