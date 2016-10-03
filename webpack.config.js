module.exports = {
  devtool: 'source-map',
  entry: [
    // Set up an ES6-ish environment
    'babel-polyfill',
    // Add your application's scripts below
    './src/client/main.js'
  ],
  output: {
    path: './src/server/',
    filename: 'bundle.js'
  },
  devServer: {
    inline: true,
    port: 3333
  },
  module: {
    loaders: [
      {
        test: /\.js$/,
        exclude: /node_modules/,
        loader: 'babel',
        query: {
          plugins: ['transform-decorators-legacy', 'transform-runtime'],
          presets: ['es2015', 'stage-0', 'react'],
        }
      }
    ]
  }
};