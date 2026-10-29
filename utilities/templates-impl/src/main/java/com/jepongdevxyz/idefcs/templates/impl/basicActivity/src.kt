/*
 *  This file is part of DevxyzIDE.
 *
 *  DevxyzIDE is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  DevxyzIDE is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *   along with DevxyzIDE.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.jepongdevxyz.idefcs.templates.impl.basicActivity

import com.jepongdevxyz.idefcs.templates.base.AndroidModuleTemplateBuilder
import com.jepongdevxyz.idefcs.templates.impl.base.baseLayoutContentMain
import com.jepongdevxyz.idefcs.templates.impl.base.materialAppBar
import com.jepongdevxyz.idefcs.templates.impl.base.materialFab
import com.jepongdevxyz.idefcs.templates.impl.indentToLevel

internal fun AndroidModuleTemplateBuilder.basicActivitySrcJava() = """
package ${data.packageName};

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import ${data.packageName}.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
	
	  private ActivityMainBinding binding;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		    binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
		    setSupportActionBar(binding.toolbar);

		    binding.fab.setOnClickListener(v ->
          Toast.makeText(MainActivity.this, "Replace with your action", Toast.LENGTH_SHORT).show()
        );
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.binding = null;
    }
}
""".trim()

internal fun AndroidModuleTemplateBuilder.basicActivitySrcKt() = """
package ${data.packageName}

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ${data.packageName}.databinding.ActivityMainBinding

public class MainActivity : AppCompatActivity() {
    
    private var _binding: ActivityMainBinding? = null
    
    private val binding: ActivityMainBinding
      get() = checkNotNull(_binding) { "Activity has been destroyed" }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setSupportActionBar(binding.toolbar)
        
        binding.fab.setOnClickListener {
            Toast.makeText(this@MainActivity, "Replace with your action", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
  """.trim()

internal fun basicActivityLayout() = """
<?xml version="1.0" encoding="utf-8"?>
<androidx.coordinatorlayout.widget.CoordinatorLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity">
        
    ${materialAppBar().indentToLevel(1)}
    
    <include layout="@layout/content_main"/>

    ${materialFab().indentToLevel(1)}

</androidx.coordinatorlayout.widget.CoordinatorLayout>
""".trim()

internal fun basicActivityContent() = baseLayoutContentMain()