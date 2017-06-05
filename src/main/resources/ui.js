(function() {
    var ExtensionService = require('live/services/extension');

    const service = {
        name: 'Random Forest Predictor (demo)',
        type: 'randomforest-demo',
        origin: 'Plugin Random Forest(demo)',
        roles: [],
        icon: '/content/plugin-random-forest-demo/icon.png',
        ui: { form: null, view: null }
    };

    ExtensionService.register(service);
})();