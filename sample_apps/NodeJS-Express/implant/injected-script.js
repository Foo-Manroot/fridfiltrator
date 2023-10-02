/**
 * Generic function to hook into an exported module function
 *
 * Args:
 *		module_name: string
 *			Name of the module to load.
 *
 *			The match is done with a partial string, so
 *			`libc.so.6` can still be found if, for example, module has the value `libc.so`.
 *
 *			If this function fins multiple matching modules, it returns -1
 *
 *		func_name: string
 *			Name of the exported function to find within the module specified before.
 *
 *			The match is done by the exact name, so "open" will not match "fopen".
 *			Only one possible match can be found.
 *
 *			If no matching name is found, this function returns -2
 *
 *		on_enter: function
 *			Function to execute right when entering the hooked function. As a parameter,
 *			it should receive an array with the arguments for the original function call.
 */
function hook_func (module_name, func_name, on_enter)
{
	let module_info = Process.enumerateModulesSync ()
			.filter (
				e => e.name.includes (module_name)
			);

	if (module_info.length != 1)
	{
		console.error (`Found multiple modules matching '${module_name}': [` +
			module_info.map (x => x.name) +
			"]"
		);

		console.error ("PANIKKK !!!");
		return -1
	}
	else
	{
		module_info = module_info [0];
	}

	let func_ptr = module_info.findExportByName (func_name);

	if (func_ptr == null)
	{
		console.error (`function '${func_name}' not found within the loaded module `+
			module_info.name
		);

		console.error ("PANIKKK !!!");
		return
	}

	Interceptor.attach (ptr (func_ptr),
	{
		onEnter: on_enter
	});

}


function hook_libc_open ()
{
	hook_func ("ld-musl-x86_64.so", "open", (args) =>
		{
			let filepath = Memory.readCString (args [0]);
			let flags = parseInt (args [1]);

			console.log (`fopen ("${filepath}", flags=${flags})`);
	});
}

function hook_libc_read ()
{
	hook_func ("ld-musl-x86_64.so", "read", (args) =>
		{
			let fd = parseInt (args [0]);
			let count = parseInt (args [2]);

	        console.log ("Look at me, ma! I'm reading! => " +
				`read(fd=${fd}, &contents, count=${count})`
			);
	});
}


hook_libc_open()
hook_libc_read()
