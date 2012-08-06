<ul>
	<li>USE WITH CAUTION: initialize (works only for small set of
	data) <br />
	<ul>
		<li><a href="init/groundStart">dev turn on debug</a></li>
	</ul>
	</li>
</ul>

<ul>
	<li>Branding
	<ul>
		<li><a href="mso/list">mso listing</a></li>
		<li>change data <br />
		http://host/admin/mso/modify?id=123&logoUrl=xx&logoClickUrl=xx&jingleUrl=xx
	</ul>
	</li>
</ul>

<ul>
	<li>Config
	<ul>
		<li><a href="config/transcodingServer">transcodingServer</a></li>
		<li><a href="config/mso">current mso</a></li>
		<li>change config, examples<br />
		http://host/admin/config/changeConfig?msoName=9x9&key=cdn&value=akamai
		<br />
		http://host/admin/config/changeConfig?msoName=5f&key=cdn&value=amazon
		<br />
		http://host/admin/config/changeConfig?msoName=5f&key=debug&value=1 <br />
		http://host/admin/config/changeConfig?msoName=5f&key=debug&value=0 <br />
		</li>
	</ul>
	</li>
</ul>

<ul>
	<li>User
	<ul>
		<li>channels a user subscribe, query either by user id or user
		token<br />
		http://host/admin/nnuser/subscription?id=123 <br />
		http://host/admin/nnuser/subscription?token=910JYOJ19kJ1JOY71021 <br />
	</ul>
	</li>
</ul>

<ul>
	<li>counter
	<ul>
		<li>create counter: http://host/admin/counter/create?name=bla</li>
		<li>add shard: http://host/admin/counter/addShard?name=bla</li>
		<li>read counter: http://host/admin/counter/get?name=bla</li>
	</ul>
	</li>
</ul>

<ul>
	<li>Category Listing
	<ul>
		<li><a href="category/list">category listing</a></li>
		<li>category listing by brand <br />
		http://host/admin/category/list?mso=xx</li>
		<li>change data <br />
		http://host/admin/category/modify?id=123&name=xxx&&isPublic=1&channelCount=123
		</li>
		<li>list every channel under a category <br />
		http://host/admin/category/channelList?category=123</li>
	</ul>
	</li>
</ul>

<ul>
	<li>MsoIpg
	<ul>
		<li>msoIpg listing by brand <br />
		http://host/admin/msoIpg/list?mso=123</li>
		<li>add msoIpg <br />
		http://host/admin/msoIpg/add?mso=123&channel=456&seq=7&type=2</li>
		<li>modify msoIpg (only 'seq' and 'type' are modifiable) <br />
		http://host/admin/msoIpg/modify?mso=123&channel=456&seq=8&type=1</li>
		<li>delete msoIpg <br />
		http://host/admin/msoIpg/delete?mso=123&channel=456</li>
	</ul>
	</li>
</ul>

<ul>
	<li>Channel listing
	<ul>
		<li><a href="channel/list">channel listing</a></li>
		<li>channel modify [IMPORTANT: if you mark channel status failed,
		make sure this channel is not in MsoIpg.]<br />
		(status: 0 success; 1 error; 2 processing) <br />
		http://host/admin/channel/modify?id=123&name=xx&status=1&programCount=123
		</li>
		<li>a channel's categories<br />
		http://host/admin/channel/listCategories?channel=123</li>
		<li>add a channel's categories<br />
		http://host/admin/channel/addCategories?channel=123&categories=111,112
		</li>
		<li>delete a channel's categories [NOTE: it is NOT channel
		deletion. delete please use modify, set status 1]<br />
		http://host/admin/channel/deleteCategories?channel=123&categories=111,112
		</li>
		<li>find any not unique source url, for debugging purpose <br />
		http://host/admin/channel/findUnUniqueSourceUrl</li>
	</ul>
	</li>
</ul>

<ul>
	<li>Program
	<ul>
		<li>program listing by channel<br />
		http://host/admin/program/list?channel=123</li>
		<li>program modify <br />
		http://host/admin/program/modify?id=123&status=0&updateDate=yyyyMMddHHmmss
		</li>
	</ul>
	</li>
</ul>

<ul>
	<li>Cache
	<ul>
		<li><a href="cache/deleteAll">deleteAll</a></li>
		<li>cache category, one mso at a time. <br />
		add, http://host/admin/cache/category?mso=123 <br />
		delete, http://host/admin/cache/category?id=123&delete=1 <br />
		list, <a href="cache/category?list=1"> list </a></li>
		<li>cache current mso [1. the host url is what current mso is 2.
		check config, current mso] <br />
		add, http://host/admin/cache/mso <br />
		delete, http://host/admin/cache/mso?delete=1 <br />
		list, <a href="cache/mso?list=1"> list </a></li>
		<li>cache channel <br />
		add all, http://host/admin/cache/channel<br />
		delete all, http://host/admin/cache/channel?delete=1<br />
		list all, http://host/admin/cache/channel?list=1<br />
		</li>
		<li>cache programs <br />
		add, http://host/admin/cache/program?channel=123<br />
		delete, http://host/admin/cache/program?channel=123&delete=123<br />
		list, http://host/admin/cache/program?channel=123&list=1 <br />
		</li>
	</ul>
	</li>
</ul>