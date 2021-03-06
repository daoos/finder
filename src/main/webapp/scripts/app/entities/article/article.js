'use strict';

angular.module('finderApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('article', {
                parent: 'entity',
                url: '/articles',
                data: {
//                    authorities: ['ROLE_USER'],
                	// 文章管理者角色可以访问
                    authorities: ['ROLE_ARTICLE_ADMIN'],
                    pageTitle: 'finderApp.article.home.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/article/articles.html',
                        controller: 'ArticleController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('article');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('article.detail', {
                parent: 'entity',
                url: '/article/{id}',
                data: {
//                    authorities: ['ROLE_USER'],
                	//不需要登录不设定角色就可以显示文章详细页面
                	authorities: [],
                    pageTitle: 'finderApp.article.detail.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/article/article-detail.html',
                        controller: 'ArticleDetailController'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('article');
                        return $translate.refresh();
                    }],
                    entity: ['$stateParams', 'Article', function($stateParams, Article) {
//                        return Article.get({id : $stateParams.id});
                    	//不使用这里查询Article数据,原因是数据异步加载文章内容取不到
                    	return null;
                    }]
                }
            })
            .state('article.new', {
                parent: 'article',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
               /* onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/article/article-dialog.html',
                        controller: 'ArticleDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    title: null,
                                    firstImg: null,
                                    firstImgContentType: null,
                                    content: null,
                                    createdDate: null,
                                    id: null
                                };
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('article', null, { reload: true });
                    }, function() {
                        $state.go('article');
                    })
                }]*/
                //不是用modal方式打开
                views: {
                    'content@': {
                    	templateUrl: 'scripts/app/entities/article/article-dialog.html',
                        controller: 'ArticleDialogController',
                    }
                },
                resolve: {
                	entity: function () {
                        return {
                            title: null,
                            firstImg: null,
                            firstImgContentType: null,
                            content: null,
                            createdDate: null,
                            id: null
                        };
                    }
                }
            })
            .state('article.edit', {
                parent: 'article',
                url: '/{id}/edit',
                data: {
//                    authorities: ['ROLE_USER'],
                	// 文章管理者角色可以访问
                    authorities: ['ROLE_ARTICLE_ADMIN'],
                },
               /* onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/article/article-dialog.html',
                        controller: 'ArticleDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['Article', function(Article) {
                                return Article.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('article', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]*/
                views: {
                    'content@': {
                    	templateUrl: 'scripts/app/entities/article/article-dialog.html',
                        controller: 'ArticleDialogController',
                    }
                },
                resolve: {
                	 entity: ['$stateParams', 'Article', function($stateParams, Article) {
                         return Article.get({id : $stateParams.id});
                     }]
                }
            })
            .state('article.delete', {
                parent: 'article',
                url: '/{id}/delete',
                data: {
//                    authorities: ['ROLE_USER'],
                	// 文章管理者角色可以访问
                    authorities: ['ROLE_ARTICLE_ADMIN'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/article/article-delete-dialog.html',
                        controller: 'ArticleDeleteController',
                        size: 'md',
                        resolve: {
                            entity: ['Article', function(Article) {
                                return Article.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('article', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
