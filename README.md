# Tiqo-L-Server

Welcome to Tiqo-l, the event-based Java backend for JavaScript.

Tiqo is super dynamic and responsive. It is based on WebSockets to make real time changes to HTML-elements!

Features:

    Realtime changes to HTML-elements
    Event-based programming on the server-side
    Super easy to use API
    Dynamic http-content-server to directly send files to the client
    Sleek and fast session management
    Super configurable
    and way more ...

 

But before you start it is essential for you to know what the exact purpose of Tiqo-L is:

Tiqo-L doesn't replace a webserver like apache or nginx!

Tiqo-L wasn't made to host the HTML files even though it might be supported in the future for small standalone projects. Tiqo-L needs a webserver to host the .html files and the JavaScript-client. The real content of the page will be delivered to the client by the server.

Can I write my complete website using Tiqo-L?

Yes you can, but I won't recommence it. Tiqo-L is made to create super responsive web-panels that need real time data (like a performance monitor or a liveticker) or for multi-user applications (like a online document editor or a card game). Using Tiqo-L for very static websites with less to no dynamic content is not recommenced since its wasted server performance (That is why tiqo-l.com is not made with Tiqo-L). The ideal usage of Tiqo-L is a classic website with Tiqo-L elements (for example in iframes).

Still want to use Tiqo-L? Great! Go to the article "Setup" on tiqo-l.com to find out how to setup your server.
