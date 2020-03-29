if ('function' === typeof importScripts) {
    importScripts('https://storage.googleapis.com/workbox-cdn/releases/5.1.2/workbox-sw.js');

    const {registerRoute} = workbox.routing;
    const {StaleWhileRevalidate, CacheFirst} = workbox.strategies;
    const {ExpirationPlugin} = workbox.expiration;

    registerRoute(
        /\.css$/,
        new StaleWhileRevalidate({
            cacheName: 'css-cache',
        })
    );

    registerRoute(
        /\.js$/,
        new StaleWhileRevalidate({
            cacheName: 'js-cache',
        })
    );

    registerRoute(
        /\.(?:png|jpg|jpeg|svg|gif)$/,
        new CacheFirst({
            cacheName: 'image-cache',
            plugins: [
                new ExpirationPlugin({
                    maxEntries: 20,
                    maxAgeSeconds: 7 * 24 * 60 * 60,
                })
            ],
        })
    );
}